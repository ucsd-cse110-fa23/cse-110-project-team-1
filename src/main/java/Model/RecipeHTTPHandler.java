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
interface RecipeHTTPHandlerInterface extends HttpHandler {
}

public class RecipeHTTPHandler implements RecipeHTTPHandlerInterface {

	protected final RecipeList list;
	protected AccountManager accountManager;
	protected GPTModel gpt;
	protected WhisperModel wisp;
	protected ImageModel dalle;

	public RecipeHTTPHandler(RecipeList list, AccountManager accountManager) {
		this.list = list;
		this.accountManager = accountManager;
		gpt = new ChatGPT();
		wisp = new Whisper();
		dalle = new Dalle();
	}

	public void handle(HttpExchange httpExchange) throws IOException {
        String response = "Request Received";
        String method = httpExchange.getRequestMethod();
        // System.out.println("Request recieved " + method);

        try {
            if (method.equals("GET")) {
                response = handleGet(httpExchange);
            } else if (method.equals("POST")) {
                response = handlePost(httpExchange);
            } else if (method.equals("PUT")) {
                response = handlePut(httpExchange);
            } else if (method.equals("DELETE")) {
                response = handleDelete(httpExchange);
            } else {
                throw new Exception("Not Valid Request Method");
            }
        } catch (Exception e) {
            System.out.println("An erroneous request");
            response = e.toString();
            e.printStackTrace();
        }
        // System.out.println("Server Sending: " + response);
        // Sending back response to the client
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
	// Is expecting a get request with an ID of a recipe and returns with the recipe
	// data
	private String handleGet(HttpExchange httpExchange) throws IOException {
		String response = "Invalid recipeID";
		URI uri = httpExchange.getRequestURI();
		String uriString = uri.toString();
		String query = uri.getRawQuery();

		Integer ownerID = verifyUserAndGetID(httpExchange);
		if (ownerID != null) {
			if (query != null) {
				if (query.contains("recipeID")) {
					String idString = query.substring(query.indexOf("=") + 1);
					int recipeID = Integer.valueOf(idString);
					Recipe r = list.getRecipe(recipeID);
					if (r != null && r.getOwnerID() == ownerID) {
						response = r.toJson().toString();
					}
				} else if (query.contains("all")) {
					response = list.getUserRecipes(ownerID).toString();
				}
			}
		}else if(uriString.substring(0,8).equals("/shared/")){
			int recipeID = Integer.parseInt(uriString.substring(14,uriString.indexOf(".")));
			if(list.getRecipe(recipeID) == null){
				System.out.println("invalid recipe ID " + recipeID);
				return "Invalid recipe ID " + recipeID;
			}
			if(!list.getRecipe(recipeID).getShared()){
				System.out.println("Private recipe["+list.getRecipe(recipeID).getShared()+"] ID " + recipeID);
				return "Private recipe ID " + recipeID;
			}
			//System.out.println("recipe req: "+ recipeID);
			FileInputStream fis = new FileInputStream("shared/recipe"+recipeID+".html");
			byte[] buffer = new byte[10];
			StringBuilder sb = new StringBuilder();
			while (fis.read(buffer) != -1) {
				sb.append(new String(buffer));
				buffer = new byte[10];
			}
			fis.close();

			//String content = sb.toString();
			response = sb.toString();
		} else {
			System.out.println("query: "+query);
			System.out.println("uri: "+uri);
			response = "Invalid username or password";
		}
		return response;
	}

	/*
	 * post requests for now just take in text until audio is done and then
	 * will get a audio file as an input stream
	 * two type of post request, mealType and ingredients
	 * post data is currently just mealType=dinner, mealType=lunch,
	 * mealType=breakfast
	 * or
	 * D,ingredients=....
	 * L,ingredients=....
	 * B,ingredients=....
	 * eventually will be a proper audio file when whisper gets set up
	 */
	private String handlePost(HttpExchange httpExchange) throws IOException {

		Headers headers = httpExchange.getRequestHeaders();
		String audioType = headers.getFirst("Audio-Type");
		String saveDirectory = "src/main/ReceivedMedia/";
		String response = "Invalid POST request";

		// Check for login/ account handling request
		if (headers.containsKey("UserHandling")) {
			String handleType = headers.get("UserHandling").get(0);
			if (handleType.equals("CREATE")) {
				response = handleCreate(httpExchange);
			} else if (handleType.equals("LOGIN")) {
				response = handleLogin(httpExchange);
			}
			return response;
		}

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
		// After obtaining transcribedAudio, get the ownerID
		Integer ownerID = verifyUserAndGetID(httpExchange);

		// Check if ownerID is not null before proceeding
		if (ownerID != null) {
			if (audioType.contains("mealType")) {
				response = transcribedAudio;
			} else if (audioType.contains("ingredients")) {
				// do gpt stuff
				String mealType = headers.getFirst("Meal-Type");
				System.out.println("Meal Type: " + mealType);
				try {
					response = gpt.getResponse(mealType, transcribedAudio);
					System.out.println("Got GPT:" + response);
					String recipeText = response.trim() + "\n";
					Integer tempRecipeID = -1;
					String recipeTitle = recipeText.substring(0, recipeText.indexOf("\n"));
					String base64Image = dalle.generateImageBase64(recipeTitle);
					Recipe newRecipe = new Recipe(tempRecipeID, recipeTitle, recipeText, mealType, ownerID,
							base64Image);
					response = newRecipe.toJson().toString();
				} catch (Exception e) {
					System.out.println("Response was" + response);
					e.printStackTrace();
				}
			}
		} else {
			response = "Invalid username or password";
		}
		// this replace makes sure no wierd control characters in the json
		// https://stackoverflow.com/questions/14028716/how-to-remove-control-characters-from-java-string
		return response.replaceAll("\\p{Cntrl}", "");
	}

	/*
	 * This will be to edit recipe
	 * it will get a json object to parse as the streamed in value
	 * 
	 */
	private String handlePut(HttpExchange httpExchange) throws IOException {
		String response = "Invalid PUT request";
		InputStream inStream = httpExchange.getRequestBody();
		String postData = new BufferedReader(new InputStreamReader(inStream)).lines().collect(Collectors.joining("\n"));

		JSONObject allRec = new JSONObject(postData);
	
		Integer ownerID = verifyUserAndGetID(httpExchange);

		if (ownerID != null) {
			if(postData.contains("shareID")){
				int rID = allRec.getInt("shareID");

				System.out.println("rUID:"+ list.getRecipe(rID).getOwnerID() +"rID:"+ rID + " uID" + ownerID);
				if(list.getRecipe(rID).getOwnerID() != ownerID){
					response = "Not your recipe ID" + rID;
				  	System.out.println("Not your recipe ID" + rID);
				}

				if(list.shareRecipe(rID)){
					response = "Shared recipe " + rID;
					System.out.println("Shared recipe " + rID);
				}else{
					response = "Invalid recipe ID " + rID;
					System.out.println("Invalid recipe ID " + rID);
				}
				list.saveToDisk(); // Save the updated list
				return response;
			}

			int recipeID = allRec.getInt("recipeID");
			String newRecipeTitle = allRec.getString("newRecipeTitle");
			String newRecipeText = allRec.getString("newRecipeText");
			String mealType = allRec.getString("mealType");
			String base64Image = allRec.getString("base64Image");
			// Check if the recipe exists
			if (list.getRecipe(recipeID) != null) {
				// Update existing recipe without generating a new image
				Recipe existingRecipe = list.getRecipe(recipeID);
				existingRecipe.setRecipeTitle(newRecipeTitle, list.getShareDirectory());
				existingRecipe.setRecipeText(newRecipeText, list.getShareDirectory());
				//existingRecipe.setMealType(mealType);
				list.saveToDisk(); // Save the updated list
				response = "" + recipeID;
			} else {
				// Generate an image and add the recipe if it's new
				try {
					recipeID = list.addRecipe(newRecipeTitle, newRecipeText, mealType, ownerID, base64Image);
					response = "" + recipeID;
				} catch (Exception e) {
					response = "Error generating image: " + e.getMessage();
					e.printStackTrace();
				}
			}
		} else {
			response = "Invalid username or password";
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
		String postData = new BufferedReader(new InputStreamReader(inStream)).lines().collect(Collectors.joining("\n"));

		if (postData != null) {
			String recipeID_String = postData.substring(postData.indexOf("=") + 1);
			int recipeID = Integer.parseInt(recipeID_String);
			Integer ownerID = verifyUserAndGetID(httpExchange);
			if (ownerID != null) {
				if (list.deleteRecipe(recipeID, ownerID)) {
					response = "Deleted recipe " + recipeID;
					System.out.println("Deleted recipe " + recipeID);
				} else {
					response = "Invalid recipe ID " + recipeID;
					System.out.println("Invalid recipe ID " + recipeID);
				}
			} else {
				response = "Invalid username or password";
			}
		}
		return response;
	}

	private String handleCreate(HttpExchange httpExchange) {
		Headers headers = httpExchange.getRequestHeaders();
		String username = headers.getFirst("Username");
		String password = headers.getFirst("Password");
		System.out.println("Handling Create");
		int userID = accountManager.addUser(username, password);
		if (userID == -1) {
			return "Username already exists. Please choose a different username.";
		} else {
			return "Account created successfully. Your user ID is " + userID;
		}
	}

	private String handleLogin(HttpExchange httpExchange) {
		Headers headers = httpExchange.getRequestHeaders();
		String username = headers.getFirst("Username");
		String password = headers.getFirst("Password");

		if (accountManager.verifyAccount(username, password)) {
			return "Login successful";
		} else {
			return "Invalid username or password";
		}
	}

	private Integer verifyUserAndGetID(HttpExchange httpExchange) {
		Headers headers = httpExchange.getRequestHeaders();
		String username = headers.getFirst("Username");
		String password = headers.getFirst("Password");

		if (accountManager.verifyAccount(username, password)) {
			return accountManager.getUserID(username, password);
		} else {
			return null;
		}
	}

}

class MockRecipeHTTPHandler extends RecipeHTTPHandler {

	public MockRecipeHTTPHandler(RecipeList list, AccountManager accountManager) {
		super(list, accountManager);
		gpt = new MockGPT();
		wisp = new MockWhisper();
		dalle = new MockDalle();
	}
}
