package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class Main {
    
    public static void main(String[] args){
        RecipeServer s = new RecipeServer();
        try {
            s.startServer();
            //server.loadServer(); // load from listName.list
        } catch (Exception e) {
            // TODO: handle exception
        }
        s.stopServer();

        //Empty
        RecipeServerInterface server = new MockRecipeServer();
        try {
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.renameServer("src/test/lists/empty");
        server.loadServer();
        try {
            String content = server.getURLData("http://localhost:8100/?all");
            System.out.println("Should be empty: " + content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        server.stopServer();
        //1 recipe
        server = new MockRecipeServer();
        try {
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.renameServer("src/test/lists/demo1");
        server.loadServer();
        try {
            String content = server.getURLData("http://localhost:8100/?all");
            System.out.println("Should be 1 entry: " + content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}