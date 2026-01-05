/*
 * Decompiled with CFR 0.152.
 */
package etmaMonitor;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Capture
extends JFrame {
    protected boolean running;
    ByteArrayOutputStream out;

    public Capture() {
        super("Capture Sound Demo");
        this.setDefaultCloseOperation(3);
        Container content = this.getContentPane();
        final JButton capture = new JButton("Capture");
        final JButton stop = new JButton("Stop");
        final JButton play = new JButton("Play");
        capture.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(false);
        ActionListener captureListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                capture.setEnabled(false);
                stop.setEnabled(true);
                play.setEnabled(false);
                Capture.this.captureAudio();
            }
        };
        capture.addActionListener(captureListener);
        content.add((Component)capture, "North");
        ActionListener stopListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                capture.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                Capture.this.running = false;
            }
        };
        stop.addActionListener(stopListener);
        content.add((Component)stop, "Center");
        ActionListener playListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Capture.this.playAudio();
            }
        };
        play.addActionListener(playListener);
        content.add((Component)play, "South");
    }

    public void captureAudio() {
        try {
            final AudioFormat format = this.getFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            final TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
            line.open(format);
            line.start();
            Runnable runner = new Runnable(){
                int bufferSize;
                byte[] buffer;
                {
                    this.bufferSize = (int)format.getSampleRate() * format.getFrameSize();
                    this.buffer = new byte[this.bufferSize];
                }

                @Override
                public void run() {
                    Capture.this.out = new ByteArrayOutputStream();
                    Capture.this.running = true;
                    try {
                        while (Capture.this.running) {
                            int count = line.read(this.buffer, 0, this.buffer.length);
                            if (count <= 0) continue;
                            Capture.this.out.write(this.buffer, 0, count);
                        }
                        Capture.this.out.close();
                    }
                    catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                        System.exit(-1);
                    }
                }
            };
            Thread captureThread = new Thread(runner);
            captureThread.start();
        }
        catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
            System.exit(-2);
        }
    }

    public void playAudio() {
        try {
            byte[] audio = this.out.toByteArray();
            ByteArrayInputStream input = new ByteArrayInputStream(audio);
            final AudioFormat format = this.getFormat();
            final AudioInputStream ais = new AudioInputStream(input, format, audio.length / format.getFrameSize());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            final SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(format);
            line.start();
            Runnable runner = new Runnable(){
                int bufferSize;
                byte[] buffer;
                {
                    this.bufferSize = (int)format.getSampleRate() * format.getFrameSize();
                    this.buffer = new byte[this.bufferSize];
                }

                @Override
                public void run() {
                    try {
                        int count;
                        while ((count = ais.read(this.buffer, 0, this.buffer.length)) != -1) {
                            if (count <= 0) continue;
                            line.write(this.buffer, 0, count);
                        }
                        line.drain();
                        line.close();
                    }
                    catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                        System.exit(-3);
                    }
                }
            };
            Thread playThread = new Thread(runner);
            playThread.start();
        }
        catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
            System.exit(-4);
        }
    }

    private AudioFormat getFormat() {
        float sampleRate = 8000.0f;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}
