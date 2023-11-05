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
        //s.stopServer();
    }
}