package Model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import View.AudioRecorder;

public class WhisperTest {
    private WhisperModel whisper;
    private AudioRecorder audioRecorder;
    private boolean supported;

    @BeforeEach
    void setUp() {
        this.whisper = new MockWhisper();
        this.audioRecorder = new AudioRecorder();
        this.supported = audioRecorder.isSupported();
    }

    @Test
    void testValidWhisperDinner() {
        if (!supported) {return;}
        CountDownLatch latch = new CountDownLatch(1);

        //Async start and stop 1.5 sec recording
        Thread t = new Thread(
            new Runnable() {
            @Override
            public void run() {
                audioRecorder.startRecording();
                try {
                    Thread.sleep(2000);
                } catch (Exception e1) {
                    System.err.println(e1);
                }
                audioRecorder.stopRecording();
                latch.countDown();
            }
            });
        t.start();

        //Wait for async tasks (recording) to finish
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            String response = whisper.getResponse(new File("dinnerrecording.wav"));
            System.out.println(response.toLowerCase());
            assertTrue(response.toLowerCase().contains("dinner"));
        } catch (Exception e) {
            System.err.println(e);
        }

        File rec = new File("recording.wav");
        assertTrue(rec.exists());
    }
}
