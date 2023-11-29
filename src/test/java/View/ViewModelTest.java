package View;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.BufferedReader;
import java.io.*;
import java.io.IOException;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;


class ViewModelTest {

    private String testLoginFileName = "testLogin.csv";
    private ViewModel viewModel = new ViewModel(null, "", null, testLoginFileName);

    @BeforeEach
    void setUp() throws IOException {
        File file = new File(testLoginFileName);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    @Test
    void TestSaveUserLogin_ValidInput() throws IOException {
        viewModel.saveUserLogin("testUser", "testPassword");

        BufferedReader reader = new BufferedReader(new FileReader(testLoginFileName));
        String line = reader.readLine();
        assertEquals("testUser,testPassword", line);
        reader.close();
    }

    @Test
    void TestSaveUserLogin_EmptyUsername() throws IOException {
        viewModel.saveUserLogin("", "testPassword");

        File file = new File(testLoginFileName);
        assertEquals(0, file.length());
    }

    @Test
    void TestSaveUserLogin_EmptyPassword() throws IOException {
        viewModel.saveUserLogin("testUser", "");

        File file = new File(testLoginFileName);
        assertEquals(0L, file.length());
    }

    @Test
    void TestGetSavedUser_ValidUser() throws IOException {
        // Write a valid user to the file
        FileWriter writer = new FileWriter(testLoginFileName);
        writer.write("testUser,testPassword\n");
        writer.close();
    
        User user = viewModel.getSavedUser();
        assertEquals("testUser", user.getUsername());
        assertEquals("testPassword", user.getPassword());
    }
    
    @Test
    void TestGetSavedUser_NoUser() throws IOException {
        // Create an empty file
        FileWriter writer = new FileWriter(testLoginFileName);
        writer.close();
    
        User user = viewModel.getSavedUser();
        assertNull(user);
    }
    
    @Test
    void TestGetSavedUser_InvalidFileContent() throws IOException {
        // Write invalid content to the file
        FileWriter writer = new FileWriter(testLoginFileName);
        writer.write("invalidContent\n");
        writer.close();
    
        assertNull(viewModel.getSavedUser());
    }

    @AfterEach
    void tearDown() {
        File file = new File(testLoginFileName);
        if (file.exists()) {
           file.delete();
        }
    }
}