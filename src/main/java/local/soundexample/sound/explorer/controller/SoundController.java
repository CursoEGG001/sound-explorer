/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.soundexample.sound.explorer.controller;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import local.soundexample.sound.explorer.model.SoundModel;
import local.soundexample.sound.explorer.view.SoundView;

/**
 *
 * @author pc
 */
public class SoundController {
    
    private final SoundModel model;
    private final SoundView view;
    
    public SoundController(SoundModel model, SoundView view) {
        this.model = model;
        this.view = view;
        
        initialize();
        setupListeners();
    }
    
    private void initialize() {
        view.setMixers(model.getMixers());
        view.updateStatus("-- Seleccione un mezclador de la lista");
    }
    
    private void setupListeners() {
        // Listener de mezclador
        view.addMixerSelectionListener(event -> {
            Mixer.Info selected = (Mixer.Info) view.getSelectedMixer();
            model.updateLinesForMixer(selected);
            view.setLines(model.getAvailableLines());
            view.updateStatus("Mezclador seleccionado: " + selected.getName());
        });

        // Listener de línea
        view.addLineSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                Line.Info selected = (Line.Info) view.getSelectedLine();
                if (selected != null) {
                    model.updateFormatsForLine(selected);
                    view.setFormats(model.getSupportedFormats());
                    view.updateStatus("Línea seleccionada: " + selected.toString());
                }
                // Resetear formato y deshabilitar botón de reproducción
                model.setSelectedFormat(null);
                view.setPlayButtonEnabled(false);
            }
        });

        // Listener de formato
        view.addFormatSelectionListener(event -> {
            
            AudioFormat format = view.getSelectedFormat();
            model.setSelectedFormat(format);
            view.setPlayButtonEnabled(format != null); // Habilitar botón si tiene formato
            view.updateStatus("Formato : " + model.getSelectedFormat());
        });

        // Listener del botón Play
        view.addPlayButtonListener(event -> {
            AudioFormat selectedFormat = model.getSelectedFormat();
            if (selectedFormat != null) {
                try {
                    model.initializeAudioLine();
                    byte[] testSound = generateTestTone();
                    view.updateStatus("Reproduciendo...");
                    model.playAudio(testSound);
                    
                } catch (LineUnavailableException e) {
                    view.updateStatus("Error: " + e.getMessage());
                }
            } else {
                view.updateStatus("Error: Selecciona un formato válido.");
            }
            
        });
    }
    
    private byte[] generateTestTone() {
        AudioFormat format = model.getSelectedFormat();
        int sampleRate = (int) format.getSampleRate();
        int channels = format.getChannels();
        int bits = format.getSampleSizeInBits();
        boolean bigEndian = format.isBigEndian();
        int frameSize = format.getFrameSize(); // Usa el frameSize del formato

        double duration = 1.0;
        double frequency = 440.0;
        int bufferSize = (int) (sampleRate * duration * frameSize); // Calcula usando frameSize
        byte[] buffer = new byte[bufferSize];
        
        double angle = 0.0;
        double step = 2.0 * Math.PI * frequency / sampleRate;
        
        for (int frame = 0; frame < (bufferSize / frameSize); frame++) {
            double value = Math.sin(angle) * 0.8;
            angle += step;

            // Generar muestra para todos los canales (necesita adaptarse a más formatos)
            for (int ch = 0; ch < channels; ch++) {
                if (bits == 16) {
                    short sample = (short) (value * Short.MAX_VALUE);
                    byte msb = (byte) ((sample >> 8) & 0xFF);
                    byte lsb = (byte) (sample & 0xFF);
                    
                    int indexOffset = frame * frameSize + ch * 2;
                    buffer[indexOffset + (bigEndian ? 0 : 1)] = msb;
                    buffer[indexOffset + (bigEndian ? 1 : 0)] = lsb;
                } else {
                    byte sample = (byte) (value * Byte.MAX_VALUE);
                    buffer[frame * frameSize + ch] = sample;
                }
            }
        }
        return buffer;
    }
}
