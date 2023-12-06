package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONException;

interface WhisperModel {
    // Take in a meal type and an arbitrary list of ingredients
    // and return a recipe formulated from ChatGPT's response
    public String getResponse(File file) throws Exception;
}

public class Whisper implements WhisperModel {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
    private static String API_KEY;
    private static final String MODEL = "whisper-1";

    public Whisper() {
        try {
            FileInputStream fs = new FileInputStream("key.txt");
            API_KEY = new String(fs.readAllBytes());
            fs.close();
        } catch(Exception e) {
            System.err.println(e);
        }
    }

    public String getResponse(File file) throws Exception {
        // Set up HTTP connection
        URL url = new URI(API_ENDPOINT).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Set up request headers
        String boundary = "Boundary-" + System.currentTimeMillis();
        connection.setRequestProperty(
            "Content-Type",
            "multipart/form-data; boundary=" + boundary
        );
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);

        // Set up output stream to write request body
        OutputStream outputStream = connection.getOutputStream();

        // Write model parameter to request body
        writeParameterToOutputStream(outputStream, "model", MODEL, boundary);

        // Write file parameter to request body
        writeFileToOutputStream(outputStream, file, boundary);

        // Write closing boundary to request body
        outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());

        // Flush and close output stream
        outputStream.flush();
        outputStream.close();

        // Get response code
        int responseCode = connection.getResponseCode();
        String response;

        // Check response code and handle response accordingly
        if (responseCode == HttpURLConnection.HTTP_OK) {
            response = handleSuccessResponse(connection);
        } else {
            response = handleErrorResponse(connection);
        }

        // Disconnect connection
        connection.disconnect();
        return response;
    }

    // Helper method to write a parameter to the output stream in multipart form data format
    private static void writeParameterToOutputStream(OutputStream outputStream, String parameterName, String parameterValue, String boundary) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(("Content-Disposition: form-data; name=\"" + parameterName + "\"\r\n\r\n").getBytes());
        outputStream.write((parameterValue + "\r\n").getBytes());
    }

    private static void writeFileToOutputStream(OutputStream outputStream, File file, String boundary) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(("Content-Disposition: form-data;" +
                            "name=\"file\"; filename=\"" +
                            file.getName() +
                            "\"\r\n").getBytes());
        outputStream.write(("Content-Type: audio/mpeg\r\n\r\n").getBytes());

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();
    }

    private static String handleSuccessResponse(HttpURLConnection connection) throws IOException, JSONException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject responseJson = new JSONObject(response.toString());
        String generatedText = responseJson.getString("text");

        return generatedText;
    }

    private static String handleErrorResponse(HttpURLConnection connection) throws IOException, JSONException {
        BufferedReader errorReader = new BufferedReader(
            new InputStreamReader(connection.getErrorStream())
        );

        String errorLine;
        StringBuilder errorResponse = new StringBuilder();
        while ((errorLine = errorReader.readLine()) != null) {
            errorResponse.append(errorLine);
        }
        errorReader.close();
        String errorResult = errorResponse.toString();

        return errorResult;
    }
}

class MockWhisper implements WhisperModel {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
    private static String API_KEY;
    private static final String MODEL = "whisper-1";

    public MockWhisper() {
        try {
            FileInputStream fs = new FileInputStream("wrongkey.txt");
            API_KEY = new String(fs.readAllBytes());
            fs.close();
        } catch(Exception e) {
            System.err.println(e);
        }
    }

    public String getResponse(File file) throws Exception {
        // Set up HTTP connection
        URL url = new URI(API_ENDPOINT).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Set up request headers
        String boundary = "Boundary-" + System.currentTimeMillis();
        connection.setRequestProperty(
            "Content-Type",
            "multipart/form-data; boundary=" + boundary
        );
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);

        // Set up output stream to write request body
        OutputStream outputStream = connection.getOutputStream();

        // Write model parameter to request body
        writeParameterToOutputStream(outputStream, "model", MODEL, boundary);

        // Write file parameter to request body
        writeFileToOutputStream(outputStream, file, boundary);

        // Write closing boundary to request body
        outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());

        // Flush and close output stream
        outputStream.flush();
        outputStream.close();

        // Get response code
        int responseCode = connection.getResponseCode();
        String response;

        // Check response code and handle response accordingly
        if (responseCode == HttpURLConnection.HTTP_OK) {
        response = handleSuccessResponse(connection);
        } else {                                                // if it actually errors out not in the intended way, return error
        response = handleErrorResponse(connection);
        if (file.getName().contains("dinner")) {
            response = "dinner,potato,butter";
        } else if (file.getName().contains("lunch")) {
            response = "lunch,noodles,soy sauce";
        } else if (file.getName().contains("breakfast")) {
            response = "breakfast,crackers,jam";
        } else {
            response = "Invalid meal type";
        }
    }

        // Disconnect connection
        connection.disconnect();
        return response;
    }

    // Helper method to write a parameter to the output stream in multipart form data format
    private static void writeParameterToOutputStream(OutputStream outputStream, String parameterName, String parameterValue, String boundary) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(("Content-Disposition: form-data; name=\"" + parameterName + "\"\r\n\r\n").getBytes());
        outputStream.write((parameterValue + "\r\n").getBytes());
    }

    private static void writeFileToOutputStream(OutputStream outputStream, File file, String boundary) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(("Content-Disposition: form-data;" +
                            "name=\"file\"; filename=\"" +
                            file.getName() +
                            "\"\r\n").getBytes());
        outputStream.write(("Content-Type: audio/mpeg\r\n\r\n").getBytes());

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();
    }

    private static String handleSuccessResponse(HttpURLConnection connection) throws IOException, JSONException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject responseJson = new JSONObject(response.toString());
        String generatedText = responseJson.getString("text");

        return generatedText;
    }

    private static String handleErrorResponse(HttpURLConnection connection) throws IOException, JSONException {
        BufferedReader errorReader = new BufferedReader(
            new InputStreamReader(connection.getErrorStream())
        );

        String errorLine;
        StringBuilder errorResponse = new StringBuilder();
        while ((errorLine = errorReader.readLine()) != null) {
            errorResponse.append(errorLine);
        }
        errorReader.close();
        String errorResult = errorResponse.toString();

        return errorResult;
    }


}