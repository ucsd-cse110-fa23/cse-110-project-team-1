package Model;
public class Main {
    public static void main(String[] args){
        RecipeServer server = new RecipeServer();
        try {
            server.startServer();
            //server.loadServer(); // load from listName.list
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }
}