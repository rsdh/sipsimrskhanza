package siprsdh;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import static jdk.nashorn.internal.objects.NativeJava.extend;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author it999
 */
public class PhoneSound {
    public PhoneSound(PhoneSoundListener psListener) {
            isStop = false;
            phoneSoundListener = psListener;
    }

    public static final String RINGING = "ringing.au";
    public static final String BUSY = "busy.au";
    public static final String DIALING = "dialing.au";

    private final String MEDIA = "media" + File.separator;
    private boolean isStop;
    private PhoneSoundListener phoneSoundListener;
    private Thread thread = null;
    public void play(String filename) {
        this.thread = new Thread(new Runnable() {
            public void run() {
                try {
                    //read audio data from whatever source (file/classloader/etc.)
                    InputStream audioSrc = new FileInputStream(MEDIA + filename);
                    //add buffer for mark/reset support
                    InputStream bufferedIn = new BufferedInputStream(audioSrc);
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);// new
                    clip.loop(
                            Clip.LOOP_CONTINUOUSLY
                    );
                    clip.start();
                    isStop = true;
                    try {
                        Thread.sleep(100);
                    } catch (Exception exIn) {
                        exIn.printStackTrace();
                    }
                    isStop = false;

                    while (true) {
                        try {
                            Thread.sleep(10);
                        } catch (Exception exIn) {
                            exIn.printStackTrace();
                        }
                        if (isStop) {
                            clip.stop();
                            break;
                        }
                    }
                } catch (Exception exc) {
                    exc.printStackTrace(System.out);
                }
                if (phoneSoundListener != null)
                    phoneSoundListener.stopedPlay();
            }
        });
        this.thread.start();
    }

    public void play(String filename, int second) {
        this.thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Calendar exp = Calendar.getInstance();
                    exp.add(Calendar.SECOND, second);

                    InputStream audioSrc = new FileInputStream(MEDIA+filename);
                    InputStream bufferedIn = new BufferedInputStream(audioSrc);
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);// new
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    clip.start();
                    isStop = true;
                    try {
                        Thread.sleep(100);
                    } catch (Exception exIn) {
                        exIn.printStackTrace();
                    }
                    isStop = false;
                    while (true) {
                        try {
                            Thread.sleep(10);
                        } catch (Exception exIn) {
                            exIn.printStackTrace();
                        }
                        if (Calendar.getInstance().compareTo(exp) > 0 && !isStop) {
                            clip.stop();
                            break;
                        }
                    }                        
                } catch (Exception exc) {
                    exc.printStackTrace(System.out);
                }
                if (phoneSoundListener != null)
                    phoneSoundListener.stopedPlay();
            }
        });
        this.thread.start();
    }

    public void play(int second) {
        this.thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Calendar exp = Calendar.getInstance();
                    exp.add(Calendar.SECOND, second);
                    isStop = true;
                    try {
                        Thread.sleep(100);
                    } catch (Exception exIn) {
                        exIn.printStackTrace();
                    }
                    isStop = false;
                    while (true) {
                        try {
                            Thread.sleep(10);
                        } catch (Exception exIn) {
                            exIn.printStackTrace();
                        }
                        if (Calendar.getInstance().compareTo(exp) > 0 && !isStop)
                            break;
                    }
                } catch (Exception exc) {
                    exc.printStackTrace(System.out);
                }
                if (phoneSoundListener != null)
                    phoneSoundListener.stopedPlay();
            }
        });
        this.thread.start();
    }

    public void stop() {
        isStop = true;
        if (this.thread != null) {
            while (this.thread.isAlive()) {
                this.isStop = true;
                try {
                    Thread.sleep(20);
                } catch (Exception exIn) {
                    exIn.printStackTrace();
                }
            }
            thread = null;
        }
    }

    public boolean isPlaying() {
        return !isStop;
    }
}
