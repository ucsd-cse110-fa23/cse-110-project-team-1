package Model;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import View.AudioRecorder;

public class AudioRecorderTest {
    private AudioRecorder audioRecorder;
    private boolean supported;

    @BeforeEach
    void setUp() {
        audioRecorder = new AudioRecorder();
        this.supported = audioRecorder.isSupported();
    }

    @Test
    @Timeout(value = 8 * 1000, unit = TimeUnit.MILLISECONDS)
    void testShortRecording() {
        if (!supported) {return;}
        CountDownLatch latch = new CountDownLatch(1);

        //Async start and stop 1.5 sec recording
        Thread t = new Thread(
            new Runnable() {
            @Override
            public void run() {
                audioRecorder.startRecording();
                try {
                    Thread.sleep(1000);
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
        File rec = new File("recording.wav");
        assertTrue(rec.exists());
    }

    @Test
    @Timeout(value = 15 * 1000, unit = TimeUnit.MILLISECONDS)
    void testLongRecording() {
        if (!supported) {return;}
        CountDownLatch latch = new CountDownLatch(1);

        //Async start and stop 10 sec recording
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
        File rec = new File("recording.wav");
        assertTrue(rec.exists());
    }
}