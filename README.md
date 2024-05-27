# Team 1 PantryPal Instructions


## Requirements: !!!

 Please create a file called `key.txt` in the root folder, and fill it with *only* your ChatGPT API Key.

 You also need an internet connection if you do not have the dependencies installed, they will be downloaded.

## How to run PantryPal Server and UI
### Windows
To run the app locally, download the repository, add your `key.txt` file, and double click the `start` file.
 > If you have extensions enabled, this will be the     `start.bat` file. 


### Mac/Linux
To run the app locally, download the repository, add your `key.txt` file, open a terminal and navigate to the root folder. run `./gradlew run`




## Running Server Only
If you want to just run the server, change line 51 in `build.gradle` from 
``` 
mainClassName = "View.App"
```
to 
``` 
mainClassName = "Model.StartServer"
```
add your `key.txt` file, and double click the `start` file.

## Application Description

After running the app, new users will be prompted to create a password protected account. Returning users will have their existing data retrieved. As the name suggests, this application is your pantry pal. The user will push a button to verbally state a desired meal type from a list of options: Breakfast, Lunch, Dinner. After processing the audio input, the user will then push a button to say the ingredients they have on hand. Then, the user will be presented with a suggested recipe with two options: If the recipe is satisfactory, the user can save it for later; otherwise, the user can refresh to generate another recipe. After saving, the recipe will be saved on the server, associated with the user account. The app also offers the option to modify existing recipes manually by editing the title or steps. 