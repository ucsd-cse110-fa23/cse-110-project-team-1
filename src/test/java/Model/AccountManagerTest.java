package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AccountManagerTest {

    private File csvFile;
    private String testCSVFileName;

    @BeforeEach
    void setUp() {
        testCSVFileName = "test.csv";
        csvFile = new File(testCSVFileName);
        if (csvFile.exists()) {
            csvFile.delete();
        }
    }

    @Test
    void testAddUser() {
        AccountManager AccountManager = new AccountManager(testCSVFileName);
        
        // Test adding a user with valid username and password
        int userID = AccountManager.addUser("testUser", "testPassword");
        assertTrue(userID > 0);
    
        // Test adding a user with the same username and password as already exists
        int sameUser = AccountManager.addUser("testUser", "testPassword");
        assertNotEquals(userID, sameUser);

    }

    @Test
    void testDeleteUser() {
        AccountManager AccountManager = new AccountManager(testCSVFileName);
        
        // Add a user and then delete it
        int userID = AccountManager.addUser("testUser", "testPassword");
        boolean isDeleted = AccountManager.deleteUser(userID);
        assertTrue(isDeleted);
    
        // Try to delete a non-existing user
        isDeleted = AccountManager.deleteUser(100);
        assertFalse(isDeleted);
    }

    @Test
    void testGetUser() {
        AccountManager AccountManager = new AccountManager(testCSVFileName);
        int userID = AccountManager.addUser("testUser", "testPassword");
    
        // Test getting a user that exists
        UserAccount user = AccountManager.getUser(userID);
        assertNotNull(user);
        assertEquals(userID, user.getUserID());
    
        // Test getting a user that does not exist
        user = AccountManager.getUser(9999);
        assertNull(user);
    }
    
    @Test
    void testGetUserID() {
        AccountManager accountManager = new AccountManager(testCSVFileName);
        int userID1 = accountManager.addUser("testUser1", "testPassword1");
        int userID2 = accountManager.addUser("testUser2", "testPassword2");
        int userID3 = accountManager.addUser("testUser3", "testPassword3");
    
        // Test getting a user ID that exists
        Integer returnedUserID1 = accountManager.getUserID("testUser1", "testPassword1");
        Integer returnedUserID2 = accountManager.getUserID("testUser2", "testPassword2");
        Integer returnedUserID3 = accountManager.getUserID("testUser3", "testPassword3");
        assertEquals(userID1, returnedUserID1);
        assertEquals(userID2, returnedUserID2);
        assertEquals(userID3, returnedUserID3);
    
        // Test getting a user ID that does not exist
        Integer returnedUserID = accountManager.getUserID("nonExistingUser", "nonExistingPassword");
        assertNull(returnedUserID);
    }
    
    @Test
    void testVerifyAccount() {
        AccountManager accountManager = new AccountManager(testCSVFileName);
        accountManager.addUser("testUser1", "testPassword1");
        accountManager.addUser("testUser2", "testPassword2");
        accountManager.addUser("testUser3", "testPassword3");
    
        // Test verifying an account that exists
        boolean isVerified1 = accountManager.verifyAccount("testUser1", "testPassword1");
        boolean isVerified2 = accountManager.verifyAccount("testUser2", "testPassword2");
        boolean isVerified3 = accountManager.verifyAccount("testUser3", "testPassword3");
        assertTrue(isVerified1);
        assertTrue(isVerified2);
        assertTrue(isVerified3);
    
        // Test verifying an account that does not exist
        boolean isVerified = accountManager.verifyAccount("nonExistingUser", "nonExistingPassword");
        assertFalse(isVerified);
    }

    @Test
    void testSaveToCSV() throws IOException {
        AccountManager accountManager = new AccountManager(testCSVFileName);
        accountManager.addUser("testUser1", "testPassword1");
        accountManager.addUser("testUser2", "testPassword2");

    
        // Save to CSV
        accountManager.saveToCSV();
    
        // Check the CSV file to make sure it saved correctly
        BufferedReader reader = new BufferedReader(new FileReader(testCSVFileName));
        String line = reader.readLine();
        assertEquals("1,testUser1,testPassword1", line);
        line = reader.readLine();
        assertEquals("2,testUser2,testPassword2", line);
        assertNull(reader.readLine()); // chekc no more lines
        reader.close();
    }
    
    @Test
    void testLoadFromCSV() throws IOException {
        File file = new File(testCSVFileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        // Create a CSV file with 1 account in it:
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testCSVFileName))) {
            writer.write("1,testUser1,testPassword1\n");
            writer.write("2,testUser2,testPassword2\n");
            writer.close();
        }
    
        AccountManager accountManager = new AccountManager(testCSVFileName);
    
        // Load from CSV
        accountManager.loadFromCSV();
    
        // Check that the user has been loaded correctly
        UserAccount user1 = accountManager.getUser(1);
        UserAccount user2 = accountManager.getUser(2);
        assertNotNull(user1);
        assertNotNull(user2);
        assertEquals(1, user1.getUserID());
        assertEquals(2, user2.getUserID());
        assertEquals("testUser1", user1.getUsername());
        assertEquals("testUser2", user2.getUsername());
        assertEquals("testPassword1", user1.getPassword());
        assertEquals("testPassword2", user2.getPassword());

    }

    @Test
    void testLoadFromCSVWithInvalidData() throws IOException {
        // Create a CSV file with invalid data:
        BufferedWriter writer = new BufferedWriter(new FileWriter(testCSVFileName));
        writer.write("invalidUserID,testUser,testPassword\n"); // userID is not an integer
        writer.close();
    
        AccountManager accountManager = new AccountManager(testCSVFileName);
    
        // Load from CSV
        accountManager.loadFromCSV();
    
        // Check that no users have been loaded
        assertNull(accountManager.getUser(1));
    }
    
    @Test
    void testLoadFromCSVWithMissingData() throws IOException {
        // Create a CSV file with bad entry:
        BufferedWriter writer = new BufferedWriter(new FileWriter(testCSVFileName));
        writer.write("1,testUser\n"); // password is missing
        writer.close();
    
        AccountManager accountManager = new AccountManager(testCSVFileName);
    
        // Load from CSV
        accountManager.loadFromCSV();
    
        // Check that no users have been loaded
        assertNull(accountManager.getUser(1));
    }

    @AfterEach
    void tearDown() {
        if (csvFile.exists()) {
            csvFile.delete();
        }
    }
}