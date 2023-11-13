package Model;

public class GenerateDemoLists {

    public static void generateAndSaveDemoList() {
        // Generate and save the first demo list
        RecipeList recipeList = new RecipeList("demo1");

        // Add demo recipes to the list
        String recipe1Title = "Beans";
        String recipe1Text = "Beans\n Ingredients:\n1 Can of Beans\n\nInstructions:\nStep 1: Put beans on plate\n";
        
        String recipe2Title = "Souffle";
        String recipe2Text = "Souffle\n Ingredients:\n2 Jars of eggs\n\nInstructions:\nStep 1: Put egg in ramen\n";
        
        String recipe3Title = "Pasta";
        String recipe3Text = "Pasta\n Ingredients:\n200g Pasta\n\nInstructions:\nStep 1: Boil pasta\n";
        
        String recipe4Title = "Salad";
        String recipe4Text = "Salad\n Ingredients:\nLettuce\nTomatoes\n\nInstructions:\nStep 1: Mix ingredients\n";
        
        String recipe5Title = "Soup";
        String recipe5Text = "Soup\n Ingredients:\n1 Can of soup\n\nInstructions:\nStep 1: Heat soup\n";
        
        String recipe6Title = "Pizza";
        String recipe6Text = "Pizza\n Ingredients:\nPizza base\nTomato sauce\nCheese\n\nInstructions:\nStep 1: Spread sauce on base\nStep 2: Sprinkle cheese\nStep 3: Bake\n";
        
        String recipe7Title = "Burgers";
        String recipe7Text = "Burgers\n Ingredients:\nBurger bun\nPatty\nLettuce\nTomato\n\nInstructions:\nStep 1: Cook patty\nStep 2: Assemble burger\n";
        
        String recipe8Title = "Chicken and Rice Soup";
        String recipe8Text = "Chicken and Rice Soup\n\nIngredients:\nCanned Chicken\nRice\nChicken broth\nOnion powder\nGarlic powder\nBlack pepper\n\nInstructions:\nStep 1: Heat the chicken broth in a pot.\nStep 2: Add the canned chicken, rice, onion powder, garlic powder, and black pepper.\nStep 3: Simmer until the rice is cooked.\n";
        
        String recipe9Title = "Noodle Stir Fry";
        String recipe9Text = "Noodle Stir Fry\n\nIngredients:\nNoodles\nSoy Sauce\nAvocado oil\nOnion powder\nGarlic powder\n\nInstructions:\nStep 1: Cook the noodles.\nStep 2: Heat the avocado oil in a pan.\nStep 3: Add the cooked noodles, soy sauce, onion powder, and garlic powder.\nStep 4: Stir fry for a few minutes.\n";
    
        String recipe10Title = "Cracker and Jam Snack";
        String recipe10Text = "Cracker and Jam Snack\n\nIngredients:\nCrackers\nJam\n\nInstructions:\nStep 1: Spread jam on the crackers.\n";
    
        String recipe11Title = "Applesauce Dressing Salad";
        String recipe11Text = "Applesauce Dressing Salad\n\nIngredients:\nApplesauce\nSalad dressing of choice\nBasil\nParsley\n\nInstructions:\nStep 1: Mix applesauce with your salad dressing.\nStep 2: Sprinkle basil and parsley on top.\n";
    
        String recipe12Title = "BBQ Chicken with Rice";
        String recipe12Text = "BBQ Chicken with Rice\n\nIngredients:\nCanned Chicken\nRice\nBBQ Sauce\n\nInstructions:\nStep 1: Heat the canned chicken in a pan with BBQ sauce.\nStep 2: Serve the BBQ chicken over cooked rice.\n";
    
        recipeList.addRecipe(recipe1Title, recipe1Text);
        recipeList.addRecipe(recipe2Title, recipe2Text);
        recipeList.addRecipe(recipe3Title, recipe3Text);
        recipeList.addRecipe(recipe4Title, recipe4Text);
        recipeList.addRecipe(recipe5Title, recipe5Text);
        recipeList.addRecipe(recipe6Title, recipe6Text);
        recipeList.addRecipe(recipe7Title, recipe7Text);
        recipeList.addRecipe(recipe8Title, recipe8Text);
        recipeList.addRecipe(recipe9Title, recipe9Text);
        recipeList.addRecipe(recipe10Title, recipe10Text);
        recipeList.addRecipe(recipe11Title, recipe11Text);
        recipeList.addRecipe(recipe12Title, recipe12Text);

        recipeList.saveToDisk();

    }
}