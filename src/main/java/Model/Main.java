package Model;
public class Main {
    public static void main(String[] args){
        Server server = new Server();
        try {
            server.startServer();
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }
}