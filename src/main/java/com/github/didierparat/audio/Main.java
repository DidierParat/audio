package com.github.didierparat.audio;

import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Main {
    public static void main(final String[] args) throws Exception {
        final AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        final TargetDataLine microphone = AudioSystem.getTargetDataLine(format);
        final byte[] data = new byte[microphone.getBufferSize() / 5];
        final ByteArrayOutputStream out  = new ByteArrayOutputStream();
        final long startTime = System.currentTimeMillis();

        microphone.open();
        microphone.start();
        System.out.println("Starting recording...");
        while (System.currentTimeMillis() - startTime < 5000) {
            final int availableBytesSize = microphone.available();
            final int bytesRead = microphone.read(data, 0, availableBytesSize);
            out.write(data, 0, bytesRead);
        }
        microphone.drain();
        int availableBytesSize = microphone.available();
        while (availableBytesSize != 0) {
            final byte[] dataToDrain = new byte[availableBytesSize];
            final int bytesRead = microphone.read(dataToDrain, 0, availableBytesSize);
            out.write(dataToDrain, 0, bytesRead);
            availableBytesSize = microphone.available();
        }
        System.out.println("Stop recording.");
        microphone.stop();

        final SourceDataLine speaker = AudioSystem.getSourceDataLine(format);
        speaker.open();
        speaker.start();
        final byte[] recordedAudio = out.toByteArray();
        int bytesWritten = 0;
        System.out.println("Start playing...");
        while (bytesWritten < recordedAudio.length) {
            int bytesToWrite = speaker.available();
            if (bytesToWrite + bytesWritten > recordedAudio.length) {
                bytesToWrite = recordedAudio.length - bytesWritten;
            }
            speaker.write(recordedAudio, bytesWritten, bytesToWrite);
            bytesWritten += bytesToWrite;
        }
        System.out.println("Stop playing.");
        speaker.stop();
    }
}
