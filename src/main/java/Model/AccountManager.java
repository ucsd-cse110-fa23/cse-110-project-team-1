package Model;

import java.util.HashMap;
import java.io.*;

public class AccountManager {
    private HashMap<Integer, UserAccount> userAccounts;
    private int highestUserID;
    private String csvFilePath;

    public AccountManager(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        userAccounts = new HashMap<>();
        highestUserID = 0;
        loadFromCSV();
    }

    public synchronized int addUser(String username, String password) {
        for (UserAccount user : userAccounts.values()) {
            if (user.getUsername().equals(username)) {
                System.out.println("Failed to create user " + username + ":" + password);
                return -1;
            }
        }
        int userID = ++highestUserID;
        UserAccount user = new UserAccount(userID, username, password);
        userAccounts.put(userID, user);
        System.out.println("Added userID: " + userID);
        saveToCSV();
        return userID;
    }

    public synchronized boolean deleteUser(int userID) {
        if (userAccounts.get(userID) != null) {
            userAccounts.remove(userID);
            System.out.println("Added userID: " + userID);
            saveToCSV();
            return true;
        }
        return false;
    }

    public UserAccount getUser(int userID) {
        return userAccounts.get(userID);
    }

    public synchronized Integer getUserID(String username, String password) {
        for (UserAccount user : userAccounts.values()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user.getUserID();
            }
        }
        return null;
    }

    public synchronized boolean verifyAccount(String username, String password) {
        for (UserAccount user : userAccounts.values()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                System.out.println("Verified user: " + username);
                return true;
            }
        }
        return false;
    }

    // Some ideas from https://stackoverflow.com/questions/14226830/java-csv-file-easy-read-write
    // and https://attacomsian.com/blog/java-read-parse-csv-file
    // https://www.w3schools.com/java/java_files_create.asp
    synchronized void loadFromCSV() {
        String line = "";
        String csvSplitBy = ",";
        boolean dataIsValid = true;
    
        // Make sure file exists
        File csvFile = new File(csvFilePath);
        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            System.out.println("Reading Saved Users");
            while ((line = br.readLine()) != null) {
                //System.out.println("Read:\"" + line + "\"");
                // use comma as separator
                String[] user = line.split(csvSplitBy);

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
            
                // Check it is formatted correctly and first entry is an Int
                if (user.length != 3 || !isInteger(user[0])) {
                    dataIsValid = false;
                    break;
                }
                try {
                    int userID = Integer.parseInt(user[0]);
                    String username = user[1];
                    String password = user[2];
                    userAccounts.put(userID, new UserAccount(userID, username, password));
                    if (userID > highestUserID) {
                        highestUserID = userID;
                    }
                } catch (NumberFormatException e) {
                    dataIsValid = false;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        if (!dataIsValid) {
            userAccounts.clear();
            highestUserID = 0;
            saveToCSV();
        }
    }

    synchronized void saveToCSV() {
        try {
            FileWriter csvWriter = new FileWriter(csvFilePath);
    
            for (UserAccount user : userAccounts.values()) {
                csvWriter.append(String.valueOf(user.getUserID()));
                csvWriter.append(",");
                csvWriter.append(user.getUsername());
                csvWriter.append(",");
                csvWriter.append(user.getPassword());
                csvWriter.append("\n");
            }
    
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

class UserAccount {
    private int userID;
    private String username;
    private String password;

    public UserAccount(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    public int getUserID() {
        return this.userID;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}