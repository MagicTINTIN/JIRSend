package com.JIRSendApp.view.sound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.JIRSendApp.controller.MainController;
import com.JIRSendApp.view.cli.Log;

public class SoundPlayer implements LineListener {

    static private String msgSoundPath = "assets/sound3.wav";
    // static private String newContactSoundPath = "/assets/sound2.wav";
    private Clip msgSoundClip = getFileClip(msgSoundPath);
    //private Clip newContactSoundClip = getFileClip(newContactSoundPath);

    public SoundPlayer() {
        MainController.messageReceived.subscribe((msg) -> {
            this.playMsgSound();
        });
        // MainController.contactsChange.subscribe((str) ->
        // {this.playNewContactSound();});
    }

    public void playMsgSound() {
        if (msgSoundClip == null)
            return;
        msgSoundClip.setMicrosecondPosition(0);
        msgSoundClip.start();
    }

    // public void playNewContactSound() {
    // newContactSoundClip.start();
    // }

    @Override
    public void update(LineEvent event) { // Not quite sure why I have to implement that to be honest..
        //if(event.getType() == LineEvent.Type.START)
        //    isplaying = true;
        //else if (event.getType() == LineEvent.Type.STOP)
        //    isplaying = false;
    }

    private Clip getFileClip(String path) {
        try {
            InputStream is = new BufferedInputStream(SoundPlayer.class.getClassLoader().getResourceAsStream(path));
            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(this);
            clip.open(ais);
            ais.close();
            return clip;
        } catch (IllegalArgumentException e) {
            Log.e("Could not load audio: no mixer available");
            return null;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            Log.e("Could not load audio \"" + path + "\": " + e);
            return null;
        }
    }
}
