package com.alex.voice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class WhisperTranscriptionService {

    @Value("${alex.whisper.binary-path}")
    private String whisperBinaryPath;

    @Value("${alex.whisper.model-path}")
    private String whisperModelPath;

    public String transcribe(File wavFile) throws TranscriptionException {
        Path outputPrefix = wavFile.toPath().resolveSibling(wavFile.getName() + "-out");

        List<String> command = List.of(
                whisperBinaryPath,
                "-m", whisperModelPath,
                "-f", wavFile.getAbsolutePath(),
                "-otxt",
                "-of", outputPrefix.toString(),
                "-nt" // no timestamps in output text
        );

        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();

            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new TranscriptionException("Whisper transcription timed out after 30 seconds.");
            }
            if (process.exitValue() != 0) {
                throw new TranscriptionException("Whisper process exited with code " + process.exitValue());
            }

            Path outputTxt = Path.of(outputPrefix + ".txt");
            if (!Files.exists(outputTxt)) {
                throw new TranscriptionException("Whisper did not produce an output transcript file.");
            }
            String text = Files.readString(outputTxt).trim();
            Files.deleteIfExists(outputTxt);
            return text;
        } catch (IOException e) {
            throw new TranscriptionException("Failed to run whisper-cli: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TranscriptionException("Transcription was interrupted.", e);
        } finally {
            wavFile.delete();
        }
    }

    public static class TranscriptionException extends Exception {
        public TranscriptionException(String message) {
            super(message);
        }

        public TranscriptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
