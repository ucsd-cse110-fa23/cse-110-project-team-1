package View;

import java.io.IOException;

public class ServerOfflineException extends IOException {
    public ServerOfflineException(String message) {
        super(message);
    }
}