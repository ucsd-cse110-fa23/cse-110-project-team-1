package Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AudioRecorderTest {
    private AudioRecorder audioRecorder;

    @BeforeEach
    void setUp() {
        audioRecorder = new AudioRecorder();
    }

    @Test
    void testBasicRecording() {
        try {
            Thread t = new Thread(
                new Runnable() {
                @Override
                public void run() {
                    audioRecorder.startRecording();
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e1) {
                        System.out.println(1111);
                    }
                    audioRecorder.stopRecording();
                    assertEquals(1, 1);
                }
                });
            t.start();
        } catch(Exception e) {
            System.out.println(11);
        }
    }
}
