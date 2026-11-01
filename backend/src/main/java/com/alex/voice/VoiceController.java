package com.alex.voice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Map;

@RestController
public class VoiceController {

    private final AudioCaptureService audioCaptureService;
    private final WhisperTranscriptionService transcriptionService;

    public VoiceController(AudioCaptureService audioCaptureService,
                            WhisperTranscriptionService transcriptionService) {
        this.audioCaptureService = audioCaptureService;
        this.transcriptionService = transcriptionService;
    }

    @PostMapping("/api/voice/listen")
    public ResponseEntity<Map<String, Object>> listen(@RequestParam(defaultValue = "5") int seconds) {
        File wavFile;
        try {
            wavFile = audioCaptureService.recordToWavFile(seconds);
        } catch (AudioCaptureService.MicrophoneUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "microphone_unavailable", "message", e.getMessage()));
        }

        try {
            String text = transcriptionService.transcribe(wavFile);
            return ResponseEntity.ok(Map.of("text", text));
        } catch (WhisperTranscriptionService.TranscriptionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "transcription_failed", "message", e.getMessage()));
        }
    }
}
