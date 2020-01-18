package com.bose.ar.scene_example.model;

import android.content.Context;
import android.media.MediaPlayer;

public class Model {

    private Soundbox up;
    private Soundbox down;
    private Soundbox left;
    private Soundbox right;

    private MediaPlayer upPlayer;
    private MediaPlayer downPlayer;
    private MediaPlayer leftPlayer;
    private MediaPlayer rightPlayer;

    private Context context;

    //constants
    public final static int UP = 1;
    public final static int DOWN = 2;
    public final static int LEFT = 3;
    public final static int RIGHT = 4;

    public Model() {

    }

    /**
     * Initializes a state containing sound for the up, down, left and right soundboxes.
     * Also makes mediaPlayers for each soundbox ready to play sounds.
     * @param context the context of the activity using this class
     * @param upId the resource id of the sound to be used by the up soundbox
     * @param downId the resource id of the sound to be used by the down soundbox
     * @param leftId the resource id of the sound to be used by the left soundbox
     * @param rightId the resource id of the sound to be used by the right soundbox
     */
    public Model(Context context, int upId, int downId, int leftId, int rightId) {

        this.up = new Soundbox(upId);
        this.down = new Soundbox(downId);
        this.left = new Soundbox(leftId);
        this.right = new Soundbox(rightId);
        this.context = context;

        upPlayer = MediaPlayer.create(context, up.getSound());
        downPlayer = MediaPlayer.create(context, down.getSound());
        leftPlayer = MediaPlayer.create(context, left.getSound());
        rightPlayer = MediaPlayer.create(context, right.getSound());

    }

    /**
     * Plays a given soundbox's sound.
     * @param soundbox an int representing which soundbox should be played.
     * @param volume a floating point value in [0.0, 1.0] that represents the volume of the sound to be played
     */
    public void playSound(int soundbox, float volume) {

        switch(soundbox) {

            case Model.UP:

                try {

                    if(upPlayer.isPlaying()) {

                        upPlayer.stop();
                        upPlayer.release();
                        upPlayer = MediaPlayer.create(context, up.getSound());

                    }

                    upPlayer.setVolume(volume, volume);
                    upPlayer.start();

                } catch(Exception e) {
                    e.printStackTrace();
                }

                break;

            case Model.DOWN:

                try {

                    if(downPlayer.isPlaying()) {

                        downPlayer.stop();
                        downPlayer.release();
                        downPlayer = MediaPlayer.create(context, down.getSound());

                    }

                    downPlayer.setVolume(volume, volume);
                    downPlayer.start();

                } catch(Exception e) {
                    e.printStackTrace();
                }

                break;

            case Model.LEFT:

                try {

                    if(leftPlayer.isPlaying()) {

                        leftPlayer.stop();
                        leftPlayer.release();
                        leftPlayer = MediaPlayer.create(context, left.getSound());

                    }

                    leftPlayer.setVolume(volume, volume);
                    leftPlayer.start();

                } catch(Exception e) {
                    e.printStackTrace();
                }

                break;


            case Model.RIGHT:

                try {

                    if(rightPlayer.isPlaying()) {

                        rightPlayer.stop();
                        rightPlayer.release();
                        rightPlayer = MediaPlayer.create(context, right.getSound());

                    }

                    rightPlayer.setVolume(volume, volume);
                    rightPlayer.start();

                } catch(Exception e) {
                    e.printStackTrace();
                }

                break;

            default:
                System.out.println("Invalid argument. Use the constants in com.bose.ar.scene_example.model.Model");
                break;

        }

    }

    /**
     * Changes the sound to be played by a given soundbox to a given sound.
     * @param soundbox the soundbox whose sound should be changed.
     * @param newSound the resource id of the sound it should be changed to.
     */
    public void changeSound(int soundbox, int newSound) {

        switch(soundbox) {

            case Model.UP:
                up.setSound(newSound);
                upPlayer = MediaPlayer.create(context, up.getSound());
                break;

            case Model.DOWN:
                down.setSound(newSound);
                downPlayer = MediaPlayer.create(context, down.getSound());
                break;

            case Model.LEFT:
                left.setSound(newSound);
                leftPlayer = MediaPlayer.create(context, left.getSound());
                break;

            case Model.RIGHT:
                right.setSound(newSound);
                rightPlayer = MediaPlayer.create(context, right.getSound());
                break;

            default:
                System.out.println("Invalid argument. Use the constants in com.bose.ar.scene_example.model.Model");
                break;

        }

    }

}
