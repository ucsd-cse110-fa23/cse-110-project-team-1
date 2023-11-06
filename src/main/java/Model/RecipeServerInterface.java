package Model;

import java.io.IOException;

/**
 * RecipeServerInterface
 */
public interface RecipeServerInterface {
  public void renameServer(String listName);
  public void loadServer();
  public void startServer() throws IOException;
  public void stopServer();
  public String getURLData(String url) throws IOException;
}