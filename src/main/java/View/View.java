package View;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;


import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

public class View {

	public BorderPane root;
	private ViewModel viewModel;

	private TextArea recipeEditArea;

	private Button addNewRecipeButton = new Button("Generate Recipe");
	private Button editSavedRecipeButton = new Button("Edit Recipe");
	private Button shareSavedRecipeButton = new Button("Share Recipe");
	private Button deleteSavedRecipeButton = new Button("Delete Recipe");
	private Button newlyGeneratedRecipeSaveButton = new Button("Save Recipe");
	private Button editedRecipeSaveButton = new Button("Save Recipe");

	private Button startRecording = new Button("Start Recording");
	private Button stopRecordingMealType = new Button("Stop Meal Type Recording");
	private Button stopRecordingIngredients = new Button("Stop Ingredient Recording");
	private Button generateNewRecipe = new Button("Generate New Recipe");
	private Button refreshRecipe = new Button("Refresh Recipe");
	private Button login = new Button("Login");
	private Button createAccount = new Button("Create Account");
	private Button logout = new Button("Logout");
	
	private ListView<HBox> allRecipes;

	private Button backToHome = new Button("Back to Home");

    private ComboBox<String> filterDropdown;

	private ComboBox<String> sortDropdown;

	private CheckBox autoLoginCheckbox;

	private ImageView recipeImageView;

	private VBox recipeTitleListleftVbox;

	private VBox homePageVbox;
	private VBox savedRecipeDetailVbox; 
	private VBox newlyGeneratedRecipeDisplayVbox;
	private VBox recordMealTypeVbox;
	private VBox recordIngredientsVbox;
	private VBox editRecipesVbox;
	private VBox loginVbox;

    private HBox filterHBox;

	private HBox sortHBox;

	//homepage labels
	private Label homePageTextHeader;
	private Label homePageTextSubheader;
	private Label savedRecipeDescription;

	//record mealtype labels
	private Label recordMealTypeText;

	//record ingredient labels
	private Label recordIngredientsText;

    //filter labels
	private String[] filters;
    private Label filterLabel;

	// sort labels
	private String[] sorts;
	private Label sortLabel;

	//Login Info Fields
	TextField usernameField;
	TextField passwordField;


	//newly generated receipe labels
	//private Label newlyGeneratedRecipeLabel;

	private int currentSelectedRecipeID;
	private RecipeNode currentSelectedRecipe;
	private RecipeNode newlyGeneratedRecipe;
	private RecipeNode currentlyEditingRecipe;
	private User user;

	private static final boolean ADD_BACK_BUTTON = true;
	private static final boolean DONT_ADD_BACK_BUTTON = false;
	private static final int SPACING = 10;
	private static final Boolean TEXT = true;
	private static final Boolean NO_TEXT = false;
	private static final Boolean MIN_HEIGHT = true;
	private static final Boolean NO_MIN_HEIGHT = false;
	
	public View(ViewModel viewModel) {
        this.viewModel = viewModel;

		this.root = new BorderPane();
        this.root.setPadding(new Insets(SPACING));

		recipeImageView = new ImageView();
		recipeImageView.setFitHeight(100);
		recipeImageView.setFitWidth(100);
		
        filterHBox = createFilters();
		sortHBox = createSorts();
        setupEventHandlers();
        setupUI();
		
	}

	/**
	 * Sets up the initial user interface of the application. 
	 * Checks if logged in already.
	 */
	private void setupUI() {
		currentSelectedRecipeID = -1;
		savedRecipeDescription = new Label("Recipe Description: ...");
		savedRecipeDescription.setWrapText(true);

		User loggedInUser = viewModel.getSavedUser();
		if(loggedInUser != null){
			this.user = loggedInUser;
			buildHomePage();
			this.refreshRecipes();
			this.updateRecipesList();
		}else{
			logout();
		}
    }

	public void refreshRecipes() {
		System.out.println("Refreshing Recipes");
		try {
			this.allRecipes = viewModel.pullRecipes(user);
		} catch (Exception e) {
			//Go back to login page
			//Clear saved login
		}
	}

	/**
	 * Updates the list of recipes displayed in the application.
	 */
	public void updateRecipesList() {
		System.out.println("Updating Recipes");
        ListView<HBox> sidebar = new ListView<HBox>();
		try {
			String filterType = filterDropdown.getValue();
			sidebar = filter(allRecipes, filterType);

			String sort = sortDropdown.getValue();
			RecipeSorter rc = new RecipeSorter(sort);
			sidebar = rc.sort(sidebar);


			this.recipeTitleListleftVbox = createRecipeTitleList(sidebar);
			this.root.setLeft(this.recipeTitleListleftVbox);
			setupSelectionListener(sidebar);
		} catch (Exception e) {
			logout();
		}
	}

	private VBox createRecipeTitleList(ListView<HBox> recipeListView) {
		VBox recipeTitleList = new VBox(SPACING, filterHBox, sortHBox, recipeListView);
		VBox.setVgrow(recipeListView, Priority.ALWAYS);
		return recipeTitleList;
	}

    private HBox createFilters() {
        filters = new String[] {"All", "Breakfast", "Lunch", "Dinner"};
        filterDropdown = new ComboBox<String>(FXCollections.observableArrayList(this.filters));
        filterDropdown.setValue(filters[0]);
        filterLabel = new Label("Filter: ");

        return new HBox(SPACING, filterLabel, filterDropdown);
    }

	private ListView<HBox> filter(ListView<HBox> allRec, String filterType){
		ListView<HBox> sidebar = new ListView<>();
		for(HBox h : allRec.getItems()) {
			// If recipe's meal type is the same as the filter,
			// or filter is "all", show the recipe on the sidebar.
			if(filterType.equalsIgnoreCase("all") || ((RecipeNode)h).toJson().getString("mealType").equalsIgnoreCase(filterType)) {
				sidebar.getItems().add(h);			// default is add at the end, which is convenient -- if want to do reverse, add at 0
			}
		}
		return sidebar;
	}
    
	private HBox createSorts() {
        sorts = new String[] {"Newest to Oldest", "Oldest to Newest", "Alphabetically", "Reverse Alphabetically"};
        sortDropdown = new ComboBox<String>(FXCollections.observableArrayList(this.sorts));
        sortDropdown.setValue(sorts[0]);
        sortLabel = new Label("Sort: ");

        return new HBox(SPACING, sortLabel, sortDropdown);
    }

	// Variable arguments for a function 
	// 
	/**
	 * This method is a general page building function that creates a VBox layout for a page in the application.
	 * This takes a variable number of nodes to add as children.
	 * Source: https://stackoverflow.com/questions/2330942/java-variable-number-of-arguments-for-a-method
	 * 
	 * @param textRegion The region where the text will be displayed on the page, can be null if addText is NO_TEXT
	 * @param minHeight The minimum height for the text region. If null, no minimum height is set.
	 * @param addText A boolean flag indicating whether to add text to the VBox. If true, textRegion is added to the VBox.
	 * @param setMinHeight A boolean flag indicating whether to set a minimum height for the text region. If true, the minHeight parameter is used.
	 * @param position The alignment position for the VBox.
	 * @param nodes The nodes to be added to the VBox. 
	 * 
	 * @return A VBox with the specified configuration.
	 */
	private VBox buildPage(Region textRegion, Integer minHeight, Boolean addText, Boolean setMinHeight, Pos position, Node... nodes) {
		VBox vbox = new VBox(SPACING);
		vbox.setAlignment(position);

		if(setMinHeight && minHeight != null){
			textRegion.setMinHeight(minHeight);
		}
		if(addText && textRegion != null){
			vbox.getChildren().add(textRegion);
		}
		vbox.getChildren().addAll(nodes);
		vbox.setPadding(new Insets(0, SPACING, 0, SPACING));
	
		return vbox;
	}

	private void buildEditPage(RecipeNode selectedRecipe){
		this.recipeEditArea = new TextArea(selectedRecipe.getRecipeText());
		HBox buttons = new HBox(backToHome, editedRecipeSaveButton);
		this.editRecipesVbox = buildPage(recipeEditArea, 400, TEXT, MIN_HEIGHT, Pos.TOP_LEFT, buttons);
		displaySelector("editPage");
	}

	private void buildDetailPage(){
		HBox buttons = new HBox(backToHome, editSavedRecipeButton, deleteSavedRecipeButton, shareSavedRecipeButton);
		Label detailMealtype = new Label("Meal Type: " + currentSelectedRecipe.getMealType());
		ScrollPane descriptionScrollBox = new ScrollPane(savedRecipeDescription);
		descriptionScrollBox.setStyle("-fx-background-color:transparent;");
		updateRecipeImage(currentSelectedRecipe.getBase64Image());
		FlowPane imagePane = new FlowPane(recipeImageView);
		this.savedRecipeDetailVbox = buildPage(null, 0, NO_TEXT, NO_MIN_HEIGHT, Pos.TOP_LEFT, detailMealtype ,descriptionScrollBox, imagePane ,buttons);
		displaySelector("recipeDetails");
	}

	private void buildNewlyGeneratedRecipeDisplay(){
		savedRecipeDescription.setText(newlyGeneratedRecipe.getRecipeText());
		HBox buttons = new HBox(backToHome, newlyGeneratedRecipeSaveButton, refreshRecipe);
		updateRecipeImage(newlyGeneratedRecipe.getBase64Image());
		FlowPane imagePane = new FlowPane(recipeImageView);
		this.newlyGeneratedRecipeDisplayVbox = buildPage(savedRecipeDescription, 0, TEXT, NO_MIN_HEIGHT, Pos.TOP_LEFT, imagePane, buttons);
		displaySelector("newlyGeneratedRecipeDisplay");
		
	}

	private void buildHomePage() {
		this.homePageTextHeader = new Label("Welcome to Pantry Pal");
		this.homePageTextHeader.setWrapText(true);

		this.homePageTextSubheader = new Label("To add a new recipe, click \"" + this.addNewRecipeButton.getText() + "\"");
		this.homePageTextSubheader.setWrapText(true);

		StackPane centeringPane = new StackPane();
		this.homePageVbox = buildPage(null, 0, NO_TEXT, NO_MIN_HEIGHT, Pos.CENTER, this.homePageTextHeader, this.homePageTextSubheader, this.generateNewRecipe, this.logout);

		VBox.setVgrow(this.homePageVbox, Priority.ALWAYS);
		centeringPane.getChildren().add(this.homePageVbox);
		StackPane.setAlignment(this.homePageVbox, Pos.CENTER);
		StackPane.setMargin(this.homePageVbox, new Insets(SPACING));
		displaySelector("home");
	}

	private void buildRecordMealType(){
		this.recordMealTypeText = new Label(
			"Click \"" +
			this.startRecording.getText() +
			"\" and then say \"breakfast\", \"lunch\", or \"dinner\" to select a meal type.\nClick \"" +
			this.stopRecordingMealType.getText() +
			"\" when you are done.");
		this.recordMealTypeText.setWrapText(true);
		this.recordMealTypeText.setMaxWidth(480.0);
		this.recordMealTypeVbox = buildPage(recordMealTypeText, 0, TEXT, NO_MIN_HEIGHT, Pos.CENTER, this.startRecording, this.stopRecordingMealType);
		displaySelector("recordMealType");
	}

	private void buildRecordIngredients(){
		this.recordIngredientsText = new Label(
			"Click \"" +
			this.startRecording.getText() + 
			"\" and speak your ingredients to generate a recipe. Click \"" +
			this.stopRecordingIngredients.getText() +
			"\" when you are done.");
		this.recordIngredientsText.setWrapText(true);
		this.recordIngredientsText.setMaxWidth(480.0);
		this.recordIngredientsVbox = buildPage(recordIngredientsText, 0, TEXT, NO_MIN_HEIGHT, Pos.CENTER, this.startRecording, this.stopRecordingIngredients);
		displaySelector("recordIngredients");
	}

	private void buildLoginPage() {
		Label welcomeMessage = new Label("Welcome to Pantry Pal");
		welcomeMessage.setWrapText(true);

		usernameField = new TextField();
		usernameField.setPromptText("Username");
		usernameField.setMaxWidth(400);
	
		passwordField = new TextField();
		passwordField.setPromptText("Password");
		passwordField.setMaxWidth(400);

		autoLoginCheckbox = new CheckBox("Remember me");

	
		this.loginVbox = buildPage(null, 0, NO_TEXT, NO_MIN_HEIGHT, Pos.CENTER, welcomeMessage, usernameField, passwordField, autoLoginCheckbox, login, createAccount);
		displaySelector("login");
	}	

	/**
	 * Displays a page in the application with an optional back button.
	 *
	 * @param vbox The layout of the page to display.
	 * @param message Debugging message.
	 * @param addBackButton If true, a back button is added to the page.
	 */
	private void displayPage(VBox vbox, String message, boolean addBackButton) {
		System.out.println(message);
	
		if (addBackButton) {
			BorderPane layout = new BorderPane();
			layout.setCenter(vbox);
	
			HBox topContainer = new HBox();
			topContainer.setAlignment(Pos.TOP_RIGHT);
			topContainer.getChildren().add(this.backToHome);
			topContainer.setPadding(new Insets(SPACING));
	
			layout.setTop(topContainer);
	
			this.root.setCenter(layout);
		} else {
			this.root.setCenter(vbox);
		}
	}

	/**
	 * This method is responsible for displaying different pages based on the provided page type.
	 * It calls the displayPage() method with different parameters depending on the page type.
	 *
	 * @param pageType The type of the page to be displayed. This can be one of the following:
	 *                 "home", "recipeDetails", "recordMealType", "recordIngredients", 
	 *                 "newlyGeneratedRecipeDisplay", "editPage".
	 *                 If the pageType is not one of these, an "Invalid page type" message is printed to the console.
	 */
	private void displaySelector(String pageType) {
		switch (pageType) {
			case "home":
				displayPage(this.homePageVbox, "Displaying Home Page", DONT_ADD_BACK_BUTTON);
				break;
			case "recipeDetails":
				displayPage(this.savedRecipeDetailVbox, "Displaying Recipe Details", DONT_ADD_BACK_BUTTON);
				break;
			case "recordMealType":
				displayPage(this.recordMealTypeVbox, "Displaying Record Meal Type Page", ADD_BACK_BUTTON);
				break;
			case "recordIngredients":
				displayPage(this.recordIngredientsVbox, "Displaying Ingredients Page", ADD_BACK_BUTTON);
				break;
			case "newlyGeneratedRecipeDisplay":
				displayPage(this.newlyGeneratedRecipeDisplayVbox, "Displaying Newly Generated Recipe Page", DONT_ADD_BACK_BUTTON);
				break;
			case "editPage":
				displayPage(this.editRecipesVbox, "Displaying Edit Page", DONT_ADD_BACK_BUTTON);
				break;
			case "login":
				displayPage(this.loginVbox, "Displaying Login Page", DONT_ADD_BACK_BUTTON);
				this.root.setLeft(null);
				break;
			default:
				System.out.println("Invalid page type: " + pageType);
				break;
		}
	}

	/**
	 * Saves the recipe and updates the view.
	 *
	 * @param recipeNode The recipe to be saved.
	 */
	private void saveRecipe(RecipeNode recipeNode){
		try {
			viewModel.performPutRequest(recipeNode,user);
			this.refreshRecipes();
			this.updateRecipesList();
			displaySelector("home");
		} catch (IOException e) {
			logout();
		}
	}


	//https://stackoverflow.com/questions/6710350/copying-text-to-the-clipboard-using-j
	private void shareRecipe(RecipeNode recipeNode){
		String s = viewModel.getServerURL() + "shared/recipe" + recipeNode.getRecipeID() + ".html";
		
		Popup.showInformation(s+" copied to clipboard");

		StringSelection stringSelection = new StringSelection(s);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);

		System.out.println("Client Share Recipe: "+currentSelectedRecipeID);
		viewModel.handleShare(currentSelectedRecipeID,user);
		//this.updateRecipes();
	}

	/**
	 * Deletes the selected recipe and updates the list.
	 */
	private void onDeleteRequest() {
		System.out.println("Requested Delete Recipe: " + currentSelectedRecipeID);
		try {
			viewModel.performDeleteRequest(currentSelectedRecipeID,user);
			this.refreshRecipes();
			this.updateRecipesList();
			displaySelector("home");
		} catch (IOException e) {
			logout();
		}
	}

	/**
	 * Handles the stop recording request based on the request type.
	 *
	 * @param requestType The type of the request.
	 * @return Returns true if the request was successful, false otherwise.
	 */
	private boolean onStopRecordRequest(String requestType) {
		//Stop Recording
		this.viewModel.stopRecording();
		//Send Request
		try {
			if(requestType.equals("mealType")){
				return viewModel.requestMealTypeCheck(user);
			}else if(requestType.equals("ingredients")){
				newlyGeneratedRecipe = viewModel.requestNewRecipe(user);
				if (newlyGeneratedRecipe == null) {
					return false;
				}
				return true;
			}
		} catch (IOException e) {
			logout();
		}
		return false;
	}

	/**
	 * Updates the details of the selected recipe.
	 *
	 * @param recipeText The text of the recipe.
	 * @param curID The ID of the recipe.
	 */
	private void updateSelectedRecipeDetails(String recipeText, Integer curID){
		savedRecipeDescription.setText(recipeText);
		currentSelectedRecipeID = curID;
	}

	/**
	 * Sets up the event handlers for button s in the application.
	 */
	private void setupEventHandlers() {
        this.deleteSavedRecipeButton.setOnAction(e -> this.onDeleteRequest());
        this.startRecording.setOnAction(e -> this.viewModel.startRecording());
        this.backToHome.setOnAction(e -> {
			((ListView<HBox>)this.recipeTitleListleftVbox.getChildren().get(2)).getSelectionModel().clearSelection();
			displaySelector("home");}
			);
        this.generateNewRecipe.setOnAction(e -> this.buildRecordMealType());
        this.editSavedRecipeButton.setOnAction(e -> this.buildEditPage(currentlyEditingRecipe));
		this.shareSavedRecipeButton.setOnAction(e -> this.shareRecipe(currentlyEditingRecipe));
        this.stopRecordingMealType.setOnAction(e -> {
            if (this.onStopRecordRequest("mealType")){
                buildRecordIngredients();
            }else{
                this.recordMealTypeText.setText(this.recordMealTypeText.getText() + "\nInvalid meal type, please try again.");
            }
        });
        this.stopRecordingIngredients.setOnAction(e -> {
            if(this.onStopRecordRequest("ingredients")){
                buildNewlyGeneratedRecipeDisplay();
            }else{
                this.recordIngredientsText.setText(this.recordIngredientsText.getText() + "\nInvalid ingredients, please try again.");
            }
        });
        this.newlyGeneratedRecipeSaveButton.setOnAction(e -> {
            saveRecipe(newlyGeneratedRecipe);
        });
        this.editedRecipeSaveButton.setOnAction(e -> {
            String newRecipeTitle = recipeEditArea.getText().trim().substring(0, recipeEditArea.getText().trim().indexOf("\n"));
            currentlyEditingRecipe.setRecipeText(recipeEditArea.getText());
            currentlyEditingRecipe.setRecipeTitle(newRecipeTitle);
            saveRecipe(currentlyEditingRecipe);
        });
		this.filterDropdown.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.updateRecipesList();
        });
		this.sortDropdown.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			this.updateRecipesList();
        });
		this.refreshRecipe.setOnAction(e -> {
			if(onStopRecordRequest("ingredients")){
				buildNewlyGeneratedRecipeDisplay();
			}
		});
		this.login.setOnAction(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			boolean loginSuccessful;
			try {
				loginSuccessful = viewModel.handleLogin(username, password);
				if (loginSuccessful) {
					if (autoLoginCheckbox.isSelected()) {
						viewModel.saveUserLogin(username, password);
					}
					this.user = new User(username,password);
					buildHomePage();
					this.refreshRecipes();
					this.updateRecipesList();
	
				}
				else {
					Popup.showError("Invalid username or password");
				}
			} 
			catch (IOException e1) {
				System.out.println("Login Error");
			}
		});
		this.createAccount.setOnAction(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			System.out.println("Create Account Button Pushed");
			boolean accountCreated;
			try {
				accountCreated = viewModel.createAccount(username, password);
				System.out.println("Account Created: " + accountCreated);
				if (accountCreated) {
					if (autoLoginCheckbox.isSelected()) {
						viewModel.saveUserLogin(username, password);
					}
					this.user = new User(username,password);
					buildHomePage();
					this.refreshRecipes();
					this.updateRecipesList();
				}
				else {
					Popup.showError("Invalid username or password when creating account");
				}
			} catch (IOException e1) {
				System.out.println("Create Account Error");
			}
		});
		this.logout.setOnAction(e -> {
			logout();
		});

    }

	/**
	 * Sets up a selection listener for sidebar.
	 * When a new item is selected it updates the recipe details and builds the detail page.
	 *
	 * @param sidebar The ListView of items
	 */
	private void setupSelectionListener(ListView<HBox> sidebar) {
		sidebar.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				RecipeNode selected = (RecipeNode)sidebar.getSelectionModel().getSelectedItem();
				currentSelectedRecipe = selected;
				String recipeText = selected.getRecipeText();
				updateSelectedRecipeDetails(recipeText, selected.getRecipeID());
				currentlyEditingRecipe = selected;
				System.out.println("Selected: " + selected.getRecipeTitle());
			}
			buildDetailPage();
		});
	}

	public BorderPane getRoot() {
		return this.root;
	}
	private void updateRecipeImage(String base64Image) {
		if (base64Image != null && !base64Image.isEmpty()) {
			recipeImageView = new ImageView(base64Image);
			recipeImageView.setFitHeight(100);
			recipeImageView.setFitWidth(100);
		} else {
			// Handle cases where there is no image
			recipeImageView.setImage(null); // Or set a default image]
			System.out.println("Image set null");
		}
    
	}
	/**
	 * Logs out the current user and displays the login page.
	 */
	private void logout() {
		viewModel.handleLogout();
		this.user = null;
		buildLoginPage();
	}
}