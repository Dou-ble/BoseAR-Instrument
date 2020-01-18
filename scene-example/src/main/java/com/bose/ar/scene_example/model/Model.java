package com.bose.ar.scene_example.model;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class Model {

    private Soundbox up;
    private Soundbox down;
    private Soundbox left;
    private Soundbox right;

    //private MediaPlayer upPlayer;
    //private MediaPlayer downPlayer;
    //private MediaPlayer leftPlayer;
    //private MediaPlayer rightPlayer;

    private int upSoundId;
    private int downSoundId;
    private int leftSoundId;
    private int rightSoundId;

    SoundPool soundPlayer;

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

        //System.out.println("Up's sound is: " + up.getSound());

        //upPlayer = MediaPlayer.create(context, up.getSound());
        //downPlayer = MediaPlayer.create(context, down.getSound());
        //leftPlayer = MediaPlayer.create(context, left.getSound());
        //rightPlayer = MediaPlayer.create(context, right.getSound());

        //build sound player.
        SoundPool.Builder builder = new SoundPool.Builder();
        builder = builder.setMaxStreams(10);
        soundPlayer = builder.build();

        upSoundId = soundPlayer.load(context, up.getSound(), 1);
        downSoundId = soundPlayer.load(context, down.getSound(), 1);
        leftSoundId = soundPlayer.load(context, left.getSound(), 1);
        rightSoundId = soundPlayer.load(context, right.getSound(), 1);

    }

    /**
     * Plays a given soundbox's sound.
     * @param soundbox an int representing which soundbox should be played.
     * @param volume a floating point value in [0.0, 1.0] that represents the volume of the sound to be played
     */
    public void playSound(int soundbox, float volume) {

        switch(soundbox) {

            case Model.UP:

                soundPlayer.play(upSoundId, volume, volume, 0, 0, (float)1.0);

                break;

            case Model.DOWN:

                soundPlayer.play(downSoundId, volume, volume, 0, 0, (float)1.0);

                break;

            case Model.LEFT:

                soundPlayer.play(leftSoundId, volume, volume, 0, 0, (float)1.0);


            case Model.RIGHT:

                soundPlayer.play(rightSoundId, volume, volume, 0, 0, (float)1.0);

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

                soundPlayer.unload(upSoundId);
                upSoundId = soundPlayer.load(context, up.getSound(), 1);

                break;

            case Model.DOWN:
                down.setSound(newSound);

                soundPlayer.unload(downSoundId);
                downSoundId = soundPlayer.load(context, down.getSound(), 1);
                break;

            case Model.LEFT:
                left.setSound(newSound);

                soundPlayer.unload(leftSoundId);
                leftSoundId = soundPlayer.load(context, left.getSound(), 1);
                break;

            case Model.RIGHT:
                right.setSound(newSound);

                soundPlayer.unload(rightSoundId);
                rightSoundId = soundPlayer.load(context, right.getSound(), 1);
                break;

            default:
                System.out.println("Invalid argument. Use the constants in com.bose.ar.scene_example.model.Model");
                break;

        }

    }

}
