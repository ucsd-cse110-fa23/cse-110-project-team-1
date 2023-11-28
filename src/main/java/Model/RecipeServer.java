package Model;

import com.sun.net.httpserver.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.*;

public class RecipeServer implements RecipeServerInterface{

  // initialize server port and hostname
  private static final int SERVER_PORT = 8100;
  private static final String SERVER_HOSTNAME = "localhost";
  private RecipeList list;
  private HttpServer server;

  public void renameServer(String listName){
    list.changeListName(listName);
  }

  public void loadServer(){
    list.loadFromDisk();
    System.out.println("Server Load");

  }

  public void startServer() throws IOException {
    System.out.println("Server Start");
    // create a thread pool to handle requests
    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    // create a recipelist to store data
   list = new RecipeList("recipe");
   list.loadFromDisk();
  //ist.addRecipe("Pasta", "Pasta\nIngredients:\nDry Noodles\nWater\nSalt\n");

   

    // create a server
    server = HttpServer.create(
        new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT),
        0);
    
    RecipeHTTPHandlerInterface requestHandler = new RecipeHTTPHandler(list, new AccountManager("accounts.csv"));
    //MyHandler myHandler = new MyHandler(data);
    // Create the context
    server.createContext("/", requestHandler);
    //server.createContext("/name", myHandler);

    // Set the executor
    server.setExecutor(threadPoolExecutor);
    // Start the server
    server.start();

    System.out.println("Server started http://"+ SERVER_HOSTNAME+ ":"+SERVER_PORT+ "/");

  }
    public void stopServer(){
      server.stop(0);
      System.out.println("Closed server");
    }

    public String getURLData(String url) throws IOException {
        URL urlObj = new URL(url); // creating a url object
        System.out.println("Opened Mock Server");
        URLConnection urlConnection = urlObj.openConnection(); // creating a urlconnection object

        // wrapping the urlconnection in a bufferedreader
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String content;
        // reading from the urlconnection using the bufferedreader
        content = bufferedReader.readLine();
        bufferedReader.close();
        return content;
    }

}