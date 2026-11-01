package com.alex.voice;

import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Service
public class AudioCaptureService {

    // Whisper.cpp expects 16kHz, 16-bit, mono PCM WAV input.
    private static final AudioFormat FORMAT = new AudioFormat(16000f, 16, 1, true, false);

    public File recordToWavFile(int seconds) throws MicrophoneUnavailableException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);
        if (!AudioSystem.isLineSupported(info)) {
            throw new MicrophoneUnavailableException("No microphone input line supports the required audio format.");
        }

        byte[] audioBytes;
        try (TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info)) {
            line.open(FORMAT);
            line.start();

            int bytesToCapture = seconds * (int) FORMAT.getSampleRate() * FORMAT.getFrameSize();
            byte[] buffer = new byte[bytesToCapture];

            int totalRead = 0;
            while (totalRead < bytesToCapture) {
                int read = line.read(buffer, totalRead, bytesToCapture - totalRead);
                if (read < 0) {
                    break;
                }
                totalRead += read;
            }
            line.stop();
            audioBytes = buffer;
        } catch (LineUnavailableException e) {
            throw new MicrophoneUnavailableException("Microphone is not available (in use by another app or disconnected).", e);
        }

        try {
            File wavFile = File.createTempFile("alex-recording-", ".wav");
            try (AudioInputStream ais = new AudioInputStream(
                    new ByteArrayInputStream(audioBytes), FORMAT, audioBytes.length / FORMAT.getFrameSize())) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
            }
            return wavFile;
        } catch (IOException e) {
            throw new MicrophoneUnavailableException("Failed to write recorded audio to a temporary WAV file.", e);
        }
    }

    public static class MicrophoneUnavailableException extends Exception {
        public MicrophoneUnavailableException(String message) {
            super(message);
        }

        public MicrophoneUnavailableException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
