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