package View;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder {
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private DataLine.Info dataLineInfo;
    private boolean supported;

    private AudioFormat getAudioFormat() {
        // the number of samples of audio per second.
        float sampleRate = 8000.0f;
        // the number of bits in each sample of a sound that has been digitized.
        int sampleSizeInBits = 16;
        // the number of audio channels in this format (1 for mono, 2 for stereo).
        int channels = 1;
        // whether the data is signed or unsigned.
        boolean signed = true;
        // whether the audio data is stored in big-endian or little-endian order.
        boolean bigEndian = true;

        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }

    public AudioRecorder() {
        this.audioFormat = this.getAudioFormat();
        this.dataLineInfo = new DataLine.Info(
                TargetDataLine.class,
                audioFormat);
        this.supported = AudioSystem.isLineSupported(new DataLine.Info(TargetDataLine.class, audioFormat));
    }

    public void startRecording() {
        if (supported == false) {return;}
        try {
            // the format of the TargetDataLine
            // the TargetDataLine used to capture audio data from the microphone
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            // the AudioInputStream that will be used to write the audio data to a file
            AudioInputStream audioInputStream = new AudioInputStream(
                    targetDataLine);

            // the file that will contain the audio data
            File audioFile = new File("recording.wav");

            Thread t = new Thread(
                new Runnable() {
                @Override
                public void run() {
                    try {
                        AudioSystem.write(
                            audioInputStream,
                            AudioFileFormat.Type.WAVE,
                            audioFile);
                    } catch (Exception e1) {
                    }
                }
                });
            t.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stopRecording() {
        if (targetDataLine == null) {return;}
        targetDataLine.stop();
        targetDataLine.close();
    }

    public boolean isSupported() {
        return supported;
    }
}