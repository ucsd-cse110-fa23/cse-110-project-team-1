package Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.util.concurrent.CountDownLatch;

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
        CountDownLatch latch = new CountDownLatch(1);

        //Do your async job
        Thread t = new Thread(
            new Runnable() {
            @Override
            public void run() {
                audioRecorder.startRecording();
                try {
                    Thread.sleep(5000);
                } catch (Exception e1) {
                    System.out.println(1111);
                }
                audioRecorder.stopRecording();
                latch.countDown();
            }
            });
        t.start();

        //Wait for api response async
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
