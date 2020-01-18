package com.bose.ar.scene_example.model;

public class Soundbox {

    //the raw resource Id of the sound that this soundbox should play.
    private int soundResourceId;

    public Soundbox() {

    }

    /**
     * Creates a sound box containing a given sound.
     * @param resId resource id of the sound to be played.
     */
    public Soundbox(int resId) {
        this.soundResourceId = resId;
    }

    /**
     * Returns the resource id of the sound this sound box contains.
     * @return
     */
    public int getSound() {

        return this.soundResourceId;

    }

    /**
     * Changes the contained sound to a given sound.
     * @param resId the resource id of the sound to set this soundbox's sound to.
     */
    public void setSound(int resId) {
        this.soundResourceId = resId;
    }

}
