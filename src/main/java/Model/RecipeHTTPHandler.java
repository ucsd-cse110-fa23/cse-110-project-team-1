package Model;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.stream.Collectors;


import org.json.JSONObject;

/**
 * RecipeHTTPHandlerInterface
 * Doenst have any methods in addition to HttpHandler
 */
interface RecipeHTTPHandlerInterface extends HttpHandler{}

public class RecipeHTTPHandler implements RecipeHTTPHandlerInterface{

 protected final RecipeList list;
 protected GPTModel gpt;
 protected WhisperModel wisp;
 
 public RecipeHTTPHandler(RecipeList list){
   this.list = list;
   gpt = new ChatGPT();
   wisp = new Whisper();

 }

 public void handle(HttpExchange httpExchange) throws IOException {
    String response = "Request Received";
    String method = httpExchange.getRequestMethod();
    
    try {
        if (method.equals("GET")) {
          response = handleGet(httpExchange);
        } else if (method.equals("POST")) {
          response = handlePost(httpExchange);
        } else if(method.equals("PUT")){
          response = handlePut(httpExchange);
        }else if(method.equals("DELETE")){
          response = handleDelete(httpExchange);
        }else{
            throw new Exception("Not Valid Request Method");
        }
      } catch (Exception e) {
        System.out.println("An erroneous request");
        response = e.toString();
        e.printStackTrace();
      }

    //Sending back response to the client
    httpExchange.sendResponseHeaders(200, response.length());
    OutputStream outStream = httpExchange.getResponseBody();
    outStream.write(response.getBytes());
    outStream.close();

 }
 /*
  * get requests are to pull recipes from server can either use 
  * http://localhost:8100/?all to get all as a json object
  * http://localhost:8100/?recipeID=123 to get specific recipe
  */
 // Is expecting a get request with an ID of a recipe and returns with the recipe data
 private String handleGet(HttpExchange httpExchange) throws IOException {
    String response = "Invalid recipeID";
    URI uri = httpExchange.getRequestURI();
    String query = uri.getRawQuery();
    if (query != null) {
      if(query.contains("recipeID")){
        String idString = query.substring(query.indexOf("=") + 1);
      int recipeID = Integer.valueOf(idString);
      Recipe r = list.getRecipe(recipeID);
      if(r != null){
        JSONObject requestBody = new JSONObject();
        requestBody.put("recipeText", r.getRecipeText());
        requestBody.put("recipeTitle", r.getRecipeTitle());
        requestBody.put("recipeID", r.getRecipeID());
        requestBody.put("mealType", r.getMealType());
        response = requestBody.toString();
      }
    }else if(query.contains("all")){
        response = list.toJSONObject().toString();      
      }
  }
    return response;
  }

  /* 
   * post requests for now just take in text until audio is done and then
   * will get a audio file as an input stream
   * two type of post request, mealType and ingredients
   * post data is currently just mealType=dinner, mealType=lunch, mealType=breakfast
   * or
   * D,ingredients=....
   * L,ingredients=....
   * B,ingredients=....
   * eventually will be a proper audio file when whisper gets set up
   */
  private String handlePost(HttpExchange httpExchange) throws IOException {

    Headers headers = httpExchange.getRequestHeaders();
    String audioType = headers.getFirst("Audio-Type");
    String saveDirectory = "src/main/RecievedMedia/";
    String response = "Invalid POST request";
    
    try (InputStream in = httpExchange.getRequestBody();
        OutputStream out = new FileOutputStream(saveDirectory + audioType + ".wav")) {
      // this is a fix for reading single bytes at a time
      // https://coderanch.com/t/676185/java/IO-byte-array-buffer
      // this is what inspired it
      byte[] buffer = new byte[1024];
      int len;
      while ((len = in.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }
    } catch (IOException e) {
      httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
      System.out.println("Error saving file: " + e.getMessage());
      return "IOException";
    }

    String transcribedAudio = "";
    try {
      transcribedAudio = wisp.getResponse(new File(saveDirectory + audioType + ".wav"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Transcribed: " + transcribedAudio);
    // This still needs to handle calling the speach to text and check it is
    // one of the valid mealTypes and then sends the responseHeader
    if(audioType.equals("mealType")){
      response = transcribedAudio;
      
    }else if(audioType.equals("ingredients")){
      //do gpt stuff
      String mealType = headers.getFirst("Meal-Type");
      System.out.println("Meal Type: " + mealType);
      try {
        response = gpt.getResponse(mealType, transcribedAudio);
        String recipeText = response.trim() + "\n";
        Integer tempRecipeID = -1;
        String recipeTitle = recipeText.substring(0, recipeText.indexOf("\n"));
        Recipe newRecipe = new Recipe(tempRecipeID, recipeTitle, recipeText, mealType);
        response = newRecipe.toJson().toString();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return response;
  }
  /*
   * This will be to edit recipe
   * it will get a json object to parse as the streamed in value
   * 
   */
  private String handlePut(HttpExchange httpExchange) throws IOException {

    String response = "Invalid PUT request";
    InputStream inStream = httpExchange.getRequestBody();
    // Scanner scanner = new Scanner(inStream);
    String postData = new BufferedReader(new InputStreamReader(inStream)).lines().collect(Collectors.joining("\n"));
    JSONObject allRec = new JSONObject(postData);
    int recipeID = allRec.getInt("recipeID");

    if (list.editRecipe(recipeID, allRec.getString("newRecipeTitle"), allRec.getString("newRecipeText"))) {
      response = Integer.toString(recipeID);
      System.out.println("Edited recipe " + recipeID);
    } else {
      recipeID = list.addRecipe(allRec.getString("newRecipeTitle"), allRec.getString("newRecipeText"), allRec.getString("mealType"));
      response = Integer.toString(recipeID);
      System.out.println("Added recipe " + recipeID);
    }
    return response;
  }

  /*
   * This will be to delete recioe
   * this expects the raw querty to be recipeID=xxxx
   */
  private String handleDelete(HttpExchange httpExchange) throws IOException {
    String response = "Invalid DELETE request";
    InputStream inStream = httpExchange.getRequestBody();
    // Scanner scanner = new Scanner(inStream);
    String postData = new BufferedReader(new InputStreamReader(inStream)).lines().collect(Collectors.joining("\n"));
    // System.out.println("testing with that post:"+ postData);
    // String query = uri.getRawQuery();
    if (postData != null) {
      String recipeID_String = postData.substring(postData.indexOf("=") + 1);
      int recipeID = Integer.parseInt(recipeID_String);
      if (list.deleteRecipe(recipeID)) {
        response = "Deleted recipe " + recipeID;
        System.out.println("Deleted recipe " + recipeID);
      } else {
        response = "Invalid recipe ID " + recipeID;
         System.out.println("Invalid recipe ID " + recipeID);
      }
    }
     return response;

  }
  
}

class MockRecipeHTTPHandler extends RecipeHTTPHandler{
 
 public MockRecipeHTTPHandler(RecipeList list){
  super(list);
  gpt = new MockGPT();
  wisp = new MockWhisper();
 }
}

