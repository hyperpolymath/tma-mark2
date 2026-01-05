// 
// Decompiled by Procyon v0.6.0
// 

package etmaHandler;

import java.io.InputStream;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Line;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.awt.Container;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.io.ByteArrayOutputStream;
import javax.swing.JFrame;

public class Capture extends JFrame
{
    protected boolean running;
    ByteArrayOutputStream out;
    
    public Capture() {
        super("Capture Sound Demo");
        this.setDefaultCloseOperation(3);
        final Container content = this.getContentPane();
        final JButton capture = new JButton("Capture");
        final JButton stop = new JButton("Stop");
        final JButton play = new JButton("Play");
        capture.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(false);
        final ActionListener captureListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                capture.setEnabled(false);
                stop.setEnabled(true);
                play.setEnabled(false);
                Capture.this.captureAudio();
            }
        };
        capture.addActionListener(captureListener);
        content.add(capture, "North");
        final ActionListener stopListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                capture.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                Capture.this.running = false;
            }
        };
        stop.addActionListener(stopListener);
        content.add(stop, "Center");
        final ActionListener playListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Capture.this.playAudio();
            }
        };
        play.addActionListener(playListener);
        content.add(play, "South");
    }
    
    public void captureAudio() {
        try {
            final AudioFormat format = this.getFormat();
            final DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            final TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
            line.open(format);
            line.start();
            final Runnable runner = new Runnable() {
                int bufferSize = (int)format.getSampleRate() * format.getFrameSize();
                byte[] buffer = new byte[this.bufferSize];
                
                @Override
                public void run() {
                    Capture.this.out = new ByteArrayOutputStream();
                    Capture.this.running = true;
                    try {
                        while (Capture.this.running) {
                            final int count = line.read(this.buffer, 0, this.buffer.length);
                            if (count > 0) {
                                Capture.this.out.write(this.buffer, 0, count);
                            }
                        }
                        Capture.this.out.close();
                    }
                    catch (final IOException e) {
                        System.err.println("I/O problems: " + e);
                        System.exit(-1);
                    }
                }
            };
            final Thread captureThread = new Thread(runner);
            captureThread.start();
        }
        catch (final LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
            System.exit(-2);
        }
    }
    
    public void playAudio() {
        try {
            final byte[] audio = this.out.toByteArray();
            final InputStream input = new ByteArrayInputStream(audio);
            final AudioFormat format = this.getFormat();
            final AudioInputStream ais = new AudioInputStream(input, format, audio.length / format.getFrameSize());
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            final SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(format);
            line.start();
            final Runnable runner = new Runnable() {
                int bufferSize = (int)format.getSampleRate() * format.getFrameSize();
                byte[] buffer = new byte[this.bufferSize];
                
                @Override
                public void run() {
                    try {
                        int count;
                        while ((count = ais.read(this.buffer, 0, this.buffer.length)) != -1) {
                            if (count > 0) {
                                line.write(this.buffer, 0, count);
                            }
                        }
                        line.drain();
                        line.close();
                    }
                    catch (final IOException e) {
                        System.err.println("I/O problems: " + e);
                        System.exit(-3);
                    }
                }
            };
            final Thread playThread = new Thread(runner);
            playThread.start();
        }
        catch (final LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
            System.exit(-4);
        }
    }
    
    private AudioFormat getFormat() {
        final float sampleRate = 8000.0f;
        final int sampleSizeInBits = 8;
        final int channels = 1;
        final boolean signed = true;
        final boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}
