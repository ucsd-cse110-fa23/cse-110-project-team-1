# Team 1 PantryPal Instructions


## Requirements: !!!

 Please create a file called `key.txt` in the root folder, and fill it with *only* your ChatGPT API Key.

## How to run PantryPal
### Windows
#### Running server and UI
To run the app with the server running locally, download the repository, add your `key.txt` file, and double click the `start` file.
 > If you have extensions enabled, this will be the     `start.bat` file. 

#### Running Server Only
If you want to just run the server, change line 51 in `build.gradle` from 
``` 
mainClassName = "View.App"
```
to 
``` 
mainClassName = "Model.StartServer"
```
add your `key.txt` file, and double click the `start` file.
### Mac/Linux