package Model;

import java.io.IOException;

public class StartServer {
    public static RecipeServer server; 
    public static void main(String[] args) {
        server = new RecipeServer();
		try {
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
