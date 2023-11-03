package Model;
public class Main {
    public static void main(String[] args){
        RecipeServer server = new RecipeServer();
        try {
            server.startServer();
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }
}