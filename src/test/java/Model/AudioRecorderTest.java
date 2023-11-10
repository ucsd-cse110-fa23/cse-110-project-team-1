package Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import View.AudioRecorder;

public class AudioRecorderTest {
    private AudioRecorder audioRecorder;

    @BeforeEach
    void setUp() {
        audioRecorder = new AudioRecorder();
    }

    @Test
    @Timeout(value = 8 * 1000, unit = TimeUnit.MILLISECONDS)
    void testShortRecording() {
        CountDownLatch latch = new CountDownLatch(1);

        //Async start and stop 1.5 sec recording
        Thread t = new Thread(
            new Runnable() {
            @Override
            public void run() {
                audioRecorder.startRecording();
                try {
                    Thread.sleep(1500);
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
    }

    @Test
    @Timeout(value = 15 * 1000, unit = TimeUnit.MILLISECONDS)
    void testLongRecording() {
        CountDownLatch latch = new CountDownLatch(1);

        //Async start and stop 10 sec recording
        Thread t = new Thread(
            new Runnable() {
            @Override
            public void run() {
                audioRecorder.startRecording();
                try {
                    Thread.sleep(10000);
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
    }
}