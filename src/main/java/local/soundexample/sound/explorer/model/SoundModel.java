/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.soundexample.sound.explorer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author pc
 */

public final class SoundModel {

    private Mixer.Info[] mixers;
    private List<Line.Info> availableLines;
    private List<AudioFormat> supportedFormats;
    private AudioFormat selectedFormat;
    private SourceDataLine audioLine;
    private Mixer.Info selectedMixerInfo; // Added to track selected mixer

    public SoundModel() {
        refreshMixers();
    }

    public void refreshMixers() {
        mixers = AudioSystem.getMixerInfo();
    }

    public Mixer.Info[] getMixers() {
        return mixers;
    }

    public void updateLinesForMixer(Mixer.Info mixerInfo) {
        this.selectedMixerInfo = mixerInfo; // Guarda el mezclador elegido
        availableLines = new ArrayList<>();
        Mixer mixer = AudioSystem.getMixer(mixerInfo);
        Line.Info[] sourceLines = mixer.getSourceLineInfo();
        availableLines.addAll(Arrays.asList(sourceLines));
    }

    public List<Line.Info> getAvailableLines() {
        return availableLines;
    }

    public void updateFormatsForLine(Line.Info lineInfo) {
        supportedFormats = new ArrayList<>();
        if (lineInfo instanceof DataLine.Info info) {
            AudioFormat[] formats = info.getFormats();
            for (AudioFormat format : formats) {
                Encoding encoding = format.getEncoding();
                float sampleRate = format.getSampleRate();
                int sampleSizeInBits = format.getSampleSizeInBits();
                int channels = format.getChannels();
                boolean bigEndian = format.isBigEndian();

                // Ajustar sampleRate y frameRate si son <=0
                float adjustedSampleRate = sampleRate > 0 ? sampleRate : 44100.0f;
                float adjustedFrameRate = adjustedSampleRate;

                // Calcular frameSize desde sampleSize y channels
                int frameSize = (sampleSizeInBits / 8) * channels;

                AudioFormat adjustedFormat = new AudioFormat(
                        encoding,
                        adjustedSampleRate,
                        sampleSizeInBits,
                        channels,
                        frameSize,
                        adjustedFrameRate,
                        bigEndian
                );
                supportedFormats.add(adjustedFormat);
            }
        }
    }

    public List<AudioFormat> getSupportedFormats() {
        return supportedFormats;
    }

    public AudioFormat getSelectedFormat() {
        return selectedFormat;
    }

    public void setSelectedFormat(AudioFormat format) {
        this.selectedFormat = format;
    }

    public void initializeAudioLine() throws LineUnavailableException {
        if (selectedFormat != null && selectedMixerInfo != null) {
            // Obtiene el mezclador del selectedMixerInfo
            Mixer mixer = AudioSystem.getMixer(selectedMixerInfo);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, selectedFormat);
            
            // Chequea que el mezclador soporte la línea elegida.
            if (mixer.isLineSupported(info)) {
                // Consigue la linea del mezclador elegido en lugar de usar AudioSystem.getLine
                audioLine = (SourceDataLine) mixer.getLine(info);
                audioLine.open(selectedFormat);
            } else {
                throw new LineUnavailableException("The selected mixer does not support this format");
            }
        } else {
            throw new LineUnavailableException("No format or mixer selected");
        }
    }

    public void playAudio(byte[] audioData) {
        if (audioLine != null && audioLine.isOpen()) {
            audioLine.start();
            audioLine.write(audioData, 0, audioData.length);
            audioLine.drain();
            audioLine.close();
        }
    }
}