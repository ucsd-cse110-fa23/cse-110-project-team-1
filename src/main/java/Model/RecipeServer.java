package Model;

import com.sun.net.httpserver.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class RecipeServer {

  // initialize server port and hostname
  private static final int SERVER_PORT = 8100;
  private static final String SERVER_HOSTNAME = "localhost";

  public void startServer() throws IOException {
    // create a thread pool to handle requests
    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    // create a recipelist to store data
   RecipeList list = new RecipeList();
   list.loadFromDisk();
    // create a server
    HttpServer server = HttpServer.create(
        new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT),
        0);
    
    RecipeHTTPHandler requestHandler = new RecipeHTTPHandler(list);
    //MyHandler myHandler = new MyHandler(data);
    // Create the context
    server.createContext("/", requestHandler);
    //server.createContext("/name", myHandler);

    // Set the executor
    server.setExecutor(threadPoolExecutor);
    // Start the server
    server.start();

    System.out.println("Server started on port " + SERVER_PORT + "\n http://localhost:8100/?all");

  }
}