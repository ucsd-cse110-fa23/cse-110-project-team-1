package Model;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;

import org.json.JSONObject;


public class RecipeHTTPHandler implements HttpHandler {


 private final RecipeList list;
 


 public RecipeHTTPHandler(RecipeList list) {
   this.list = list;
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
 // Is expecting a get request with an ID of a recipe and returns with the recipe data
 private String handleGet(HttpExchange httpExchange) throws IOException {
    String response = "Invalid recipeID";
    URI uri = httpExchange.getRequestURI();
    String query = uri.getRawQuery();
    if (query != null) {
      String idString = query.substring(query.indexOf("=") + 1);
      int recipeID = Integer.valueOf(idString);
      Recipe r = list.getRecipe(recipeID);
      if(r != null){
        JSONObject requestBody = new JSONObject();
        requestBody.put("recipeText", r.getRecipeText());
        requestBody.put("recipeTitle", r.getRecipeTitle());
        requestBody.put("recipeID", r.getRecipeID());
        response = requestBody.toString();
      }
    }
    return response;
  }

  private String handlePost(HttpExchange httpExchange) throws IOException {
    InputStream inStream = httpExchange.getRequestBody();
    Scanner scanner = new Scanner(inStream);
    String postData = scanner.nextLine();
    String language = postData.substring(
      0,
      postData.indexOf(",")
    ), year = postData.substring(postData.indexOf(",") + 1);
 
 
    // Store data in hashmap
    data.put(language, year);
 
 
    String response = "Posted entry {" + language + ", " + year + "}";
    System.out.println(response);
 
 
    return response;
  }

  private String handlePut(HttpExchange httpExchange) throws IOException {
    InputStream inStream = httpExchange.getRequestBody();
    Scanner scanner = new Scanner(inStream);
    String postData = scanner.nextLine();
    String language = postData.substring(
      0,
      postData.indexOf(",")
    ), year = postData.substring(postData.indexOf(",") + 1);
 
    if(data.get(language) != year){
         data.put(language, year);
         String response = "Updated entry {" + language + ", " + year + "}";
         System.out.println(response);
         return response;
    }

    // Store data in hashmap
    data.put(language, year);
 
 
    String response = "Posted entry {" + language + ", " + year + "}";
    System.out.println(response);
 
 
    return response;
  }


  private String handleDelete(HttpExchange httpExchange) throws IOException {
    String response = "Invalid DELETE request";
    URI uri = httpExchange.getRequestURI();
    String query = uri.getRawQuery();
    if (query != null) {
      String value = query.substring(query.indexOf("=") + 1);
      String year = data.get(value); // Retrieve data from hashmap
      if (year != null) {
        response = "Deleted entry {" + value + "," + year + "}";
        data.remove(value);
        System.out.println(response);
      } else {
        response = "No data found for " + value;
      }
    }
     return response;
   
    //return "";
  }
  
}
