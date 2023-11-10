package View;

import java.util.Iterator;

import org.json.JSONObject;

import Controller.RequestHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;


public class ViewModel {
	/*
	 * Pulls recipies from server http://localhost:8100/
	 * Returns a Listview of Recipes in order from newest to oldest
	 */
    public static ListView<HBox> pullRecipes(){
		RequestHandler req = new RequestHandler();
		String allRecipes = req.performGET("http://localhost:8100/?all");
		JSONObject allRec = new JSONObject(allRecipes);
		Iterator<String> keys = allRec.keys();
		ListView<HBox> listView = new ListView<>();
            while(keys.hasNext()) {
				String key = keys.next();
				JSONObject r = allRec.getJSONObject(key);
                //System.out.println(allRec.getJSONObject(key));
                //System.out.println(r.getString("recipeTitle"));
				
				RecipeNode newRecipe = new RecipeNode(r.getInt("recipeID"),
													  r.getString("recipeTitle"),
													  r.getString("recipeText"));

				newRecipe.getChildren().add(new Label(newRecipe.getRecipeTitle()));
                //System.out.println(newRecipe.toString());
				listView.getItems().add(0,newRecipe);

			}
		return listView;
		//System.out.println(allRec.toString());
	}
}
