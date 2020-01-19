package com.bose.ar.scene_example;

//
//  MainFragment.java
//  BoseWearable
//
//  Created by Tambet Ingo on 11/14/2018.
//  Copyright © 2018 Bose Corporation. All rights reserved.
//

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bose.ar.scene_example.model.Model;
import com.bose.bosewearableui.DeviceConnectorActivity;
import com.bose.scene_example.R;
import com.bose.wearable.sensordata.GestureData;
import com.bose.wearable.sensordata.QuaternionAccuracy;
import com.bose.wearable.sensordata.SensorValue;
import com.bose.wearable.sensordata.Vector;
import com.bose.wearable.services.wearablesensor.ProductInfo;
import com.bose.wearable.services.wearablesensor.SensorType;
import com.bose.wearable.services.wearablesensor.WearableDeviceInformation;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;

import java.util.Locale;
import java.lang.Math;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;


public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();

    private SceneView mSceneView;
    private TextView mValuesView;
    private TextView mAccuracyView;
    private TextView mDirectionView;

    // Tutorial Objects
    private int tutorialStep;
    private ConstraintLayout tutorialLayout;
    private ImageButton tutorialExitBtn;
    private ImageButton rightArrowBtn;
    private ImageButton leftArrowBtn;
    private TextView tutorialTxt;
    private TextView tutorialTitleTxt;
    private TextView tutorialNumTxt;
    private ImageView tutorialImg;

    private SensorViewModel mViewModel;

    Model soundModel;
    private Node mProductNode;

    private boolean isLeftPlayed;
    private boolean isRightPlayed;
    private boolean isUpPlayed;
    private boolean isDownPlayed;
    private double centerP = 0.0;
    private double centerY = 0.0;

    int soundboxSide; //which soundbox side has been selected.

    public ChooserFragment cfrag;

    private long startTime;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        soundModel = new Model(getContext(), R.raw.snare_acoustic, R.raw.kick_acoustic, R.raw.hat_acoustic, R.raw.cymbal_acoustic);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View topLevelView = inflater.inflate(R.layout.fragment_main, container, false);

        View.OnClickListener closeOverlay = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View soundSelector = topLevelView.findViewById(R.id.soundSelector);
                soundSelector.setVisibility(View.GONE);
                enableButtons(topLevelView);
            }
        };

        View.OnClickListener openOverlay = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View soundSelector = topLevelView.findViewById(R.id.soundSelector);
                soundSelector.setVisibility(View.VISIBLE);

                switch(view.getId()) {

                    case R.id.upBtn:
                        soundboxSide = Model.UP;
                        break;

                    case R.id.downBtn:
                        soundboxSide = Model.DOWN;
                        break;

                    case R.id.leftBtn:
                        soundboxSide = Model.LEFT;
                        break;

                    case R.id.rightBtn:
                        soundboxSide = Model.RIGHT;
                        break;

                }
                disableButtons(topLevelView);
            }
        };

        View.OnClickListener changeSound = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedId = view.getId();

                switch(clickedId) {

                    case R.id.ac_kick_button:
                        soundModel.changeSound(soundboxSide, R.raw.kick_acoustic);
                        break;

                    case R.id.ac_snare_button:
                        soundModel.changeSound(soundboxSide, R.raw.snare_acoustic);
                        break;

                    case R.id.ac_cymbal_button:
                        soundModel.changeSound(soundboxSide, R.raw.cymbal_acoustic);
                        break;

                    case R.id.ac_hat_button:
                        soundModel.changeSound(soundboxSide, R.raw.hat_acoustic);
                        break;

                    case R.id.el_kick_button:
                        soundModel.changeSound(soundboxSide, R.raw.kick_electro);
                        break;

                    case R.id.el_snare_button:
                        soundModel.changeSound(soundboxSide, R.raw.snare_electro);
                        break;

                    case R.id.el_hat_button:
                        soundModel.changeSound(soundboxSide, R.raw.hat_electro);
                        break;

                    case R.id.el_cymbal_button:
                        soundModel.changeSound(soundboxSide, R.raw.cymbal_electro);
                        break;

                }

                topLevelView.findViewById(R.id.close_overlay).callOnClick();
            }
        };

        topLevelView.findViewById(R.id.close_overlay).setOnClickListener(closeOverlay);

        topLevelView.findViewById(R.id.upBtn).setOnClickListener(openOverlay);
        topLevelView.findViewById(R.id.downBtn).setOnClickListener(openOverlay);
        topLevelView.findViewById(R.id.leftBtn).setOnClickListener(openOverlay);
        topLevelView.findViewById(R.id.rightBtn).setOnClickListener(openOverlay);

        topLevelView.findViewById(R.id.ac_kick_button).setOnClickListener(changeSound);
        topLevelView.findViewById(R.id.ac_snare_button).setOnClickListener(changeSound);
        topLevelView.findViewById(R.id.ac_cymbal_button).setOnClickListener(changeSound);
        topLevelView.findViewById(R.id.ac_hat_button).setOnClickListener(changeSound);
        topLevelView.findViewById(R.id.el_kick_button).setOnClickListener(changeSound);
        topLevelView.findViewById(R.id.el_snare_button).setOnClickListener(changeSound);
        topLevelView.findViewById(R.id.el_cymbal_button).setOnClickListener(changeSound);
        topLevelView.findViewById(R.id.el_hat_button).setOnClickListener(changeSound);

        return topLevelView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        mSceneView = view.findViewById(R.id.scene_view);
        mValuesView = view.findViewById(R.id.valuesText);
        mAccuracyView = view.findViewById(R.id.accuracyText);
        mDirectionView = view.findViewById(R.id.directionText);

        tutorialStep = 1;

        final AppCompatImageButton calibrateButton = view.findViewById(R.id.calibrateBtn);
        calibrateButton.setOnClickListener(b -> onCalibrateClicked());

        final AppCompatImageButton connectButton = view.findViewById(R.id.connectBtn);
        connectButton.setOnClickListener(b -> onSearchClicked());

        final AppCompatImageButton tutorialBtn = view.findViewById(R.id.tutorialBtn);
        tutorialBtn.setOnClickListener(b -> showTutorial());
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(requireActivity()).get(SensorViewModel.class);
        mViewModel.wearableDeviceInfo()
            .observe(this, wearableDeviceInfo -> {
                if (wearableDeviceInfo != null) {
                    onWearableDeviceInfo(wearableDeviceInfo);
                }
            });

        this.startTime = System.currentTimeMillis();

        mViewModel.sensorData()
                .observe(this, this::updatePosition);

        //mViewModel.accelerometerData()
        //        .observe(this, this::onAccelerometerData);

        mViewModel.gestureData()
                .observe(this, this::onCalibrateClicked);

        mViewModel.sensorAccuracy()
            .observe(this, this::updateAccuracy);

        readPrefs(PreferenceManager.getDefaultSharedPreferences(requireContext()));
    }

    private void onCalibrateClicked(GestureData gestureData) {
        mViewModel.resetInitialReading();
        centerP = 0;
        centerY = 0;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private void onAccelerometerData(@Nullable SensorValue sensorValue) {
//        //final Vector vector = sensorValue.vector();
//        //System.out.println("x = " + vector.x() + ", y = " + vector.y() + ", z = " + vector.z());
//        ARCoreSensorValueReader r = new ARCoreSensorValueReader();
//        Vector3 v3 = r.vector(sensorValue);
//        System.out.println(sensorValue.quaternion());
//        System.out.println(v3);
//    }

    private void showSettings() {
        final View view = getView();
        if (view != null) {
            Navigation.findNavController(view)
                .navigate(R.id.action_mainFragment_to_mainSettingsFragment);
        }
    }

    private void addRenderable(@NonNull final Renderable renderable,
                               @NonNull final ProductInfo productInfo) {
        final Scene scene = mSceneView.getScene();

        mProductNode = new Node();
        mProductNode.setParent(scene);
        mProductNode.setLocalPosition(new Vector3(0f, 0f, -3f));
        mProductNode.setName(productInfo.toString());
        mProductNode.setRenderable(renderable);

        scene.addChild(mProductNode);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            mSceneView.resume();
        } catch (CameraNotAvailableException e) {
            Log.e(TAG, "Could not resume SceneView", e);
        }
    }

    @Override
    public void onPause() {
        mSceneView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mSceneView.destroy();
        super.onDestroy();
    }

    private void showError(@NonNull final String message) {
        final Activity activity = getActivity();
        if (activity instanceof ErrorDisplay) {
            final ErrorDisplay errorDisplay = (ErrorDisplay) activity;
            errorDisplay.showError(message);
        } else {
            Log.e(TAG, message);
        }
    }

    private void onWearableDeviceInfo(@NonNull final WearableDeviceInformation deviceInfo) {
        final ProductInfo productInfo = deviceInfo.productInfo();
        int sourceId = R.raw.alto;
        switch (productInfo.id()) {
            case ProductInfo.PRODUCT_BOSE_FRAMES:
                Log.d(TAG, "Loading Bose frames asset");
                switch (productInfo.variant()) {
                    case ProductInfo.BOSE_FRAMES_ALTO:
                        sourceId = R.raw.alto;
                        break;
                    case ProductInfo.BOSE_FRAMES_RONDO:
                        sourceId = R.raw.rondo;
                        break;
                }
                break;
            case ProductInfo.PRODUCT_DIREWOLF:
                Log.d(TAG, "Loading Direwolf asset");
                switch (productInfo.variant()) {
                    case ProductInfo.DIREWOLF_BLACK:
                        sourceId = R.raw.qc35ii_black;
                        break;
                    case ProductInfo.DIREWOLF_SILVER:
                        sourceId = R.raw.qc35ii_silver;
                        break;
                }
                break;
            case ProductInfo.PRODUCT_GOODYEAR:
                Log.d(TAG, "Loading Goodyear asset");
                sourceId = R.raw.qc35ii_black;
                break;
            default:
                Log.d(TAG, "Unhandled product " + productInfo.idName() + ", loading Bose frames asset");
                break;
        }

        ModelRenderable.builder()
            .setSource(requireContext(), sourceId)
            .build()
            .thenAccept(renderable -> addRenderable(renderable, productInfo))
            .exceptionally(throwable -> {
                Log.e(TAG, "Could not load renderable", throwable);
                showError("Unable to load " + productInfo.idName() + " renderable");
                return null;
            });
    }

    private void updatePosition(@Nullable final Quaternion quaternion) {
        final String text;
        if (mProductNode != null && quaternion != null) {
            mProductNode.setLocalRotation(quaternion);

            final boolean inverted = mViewModel.inverted();
            final double pitch = ARCoreSensorValueReader.pitch(quaternion) * (inverted ? -1 : 1);
            final double roll = ARCoreSensorValueReader.roll(quaternion) * (inverted ? -1 : 1);
            final double yaw = ARCoreSensorValueReader.yaw(quaternion) * (inverted ? -1 : 1);

            text = getString(R.string.values_format, formatAngle(pitch), formatAngle(roll), formatAngle(yaw));

            playInstrument(formatAngleDouble(pitch), formatAngleDouble(roll), formatAngleDouble(yaw));
        } else {
            text = "";
        }

        mValuesView.setText(text);
    }

    private void updateAccuracy(@Nullable final QuaternionAccuracy accuracy) {
        final String text;
        if (accuracy != null) {
            text = getString(R.string.accuracy_format, formatAngle(accuracy.estimatedAccuracy()));
        } else {
            text = "";
        }
        mAccuracyView.setText(text);
    }

    private String formatAngle(final double radians) {
        final double degrees = radians * 180 / Math.PI;
        return String.format(Locale.US, "%.2f°", degrees);
    }

    private double formatAngleDouble(final double radians) {
        final double degrees = radians * 180 / Math.PI;
        return degrees;
    }

    private void readPrefs(@NonNull final SharedPreferences preferences) {
        mViewModel.correctedInitially(preferences.getBoolean(MainSettingsFragment.PREF_CORRECTED_INITIALLY, true));
        mViewModel.inverted(preferences.getBoolean(MainSettingsFragment.PREF_MIRROR, true));
    }

    private void playInstrument(final double pitch, final double roll, final double yaw) {
        // these if statements play sound if head reaches certain positions llll
        if (pitch <= centerP - 4 && !isDownPlayed) {
            isDownPlayed = true;
            mDirectionView.setText("Down");
            float vol = velocity(Math.abs(pitch));
            soundModel.playSound(Model.DOWN, vol);
            //play sound and indicate on screen that down played
        } else if (pitch >= centerP + 4 && !isUpPlayed) {
            isUpPlayed = true;
            mDirectionView.setText("Up");
            float vol = velocity(Math.abs(pitch));
            soundModel.playSound(Model.UP, vol);
            //play up sound and indicate on screen
        } else if (yaw <= centerY - 6 && !isLeftPlayed) {
            isLeftPlayed = true;
            mDirectionView.setText("Left");
            float vol = velocity(Math.abs(yaw));
            soundModel.playSound(Model.LEFT, vol);
            //play left sound and indicate on screen
        } else if (yaw >= centerY + 6 && !isRightPlayed) {
            isRightPlayed = true;
            mDirectionView.setText("Right");
            float vol = velocity(Math.abs(yaw));
            soundModel.playSound(Model.RIGHT, vol);
            // play right sound and indicate on screen
        }

        // these if statements reset soundboxes if head reaches certain positions
        if (pitch > centerP - 4 && isDownPlayed) {
            isDownPlayed = false;
            isUpPlayed = false;
            isLeftPlayed = false;
            isRightPlayed = false;
            mDirectionView.setText("Center");
            centerP = pitch;
            centerY = yaw;
            //indicate soundbox off on screen
            this.startTime = System.currentTimeMillis();
        } else if (pitch <= centerP + 3 && isUpPlayed) {
            isUpPlayed = false;
            isDownPlayed = false;
            isLeftPlayed = false;
            isRightPlayed = false;
            mDirectionView.setText("Center");
            centerP = pitch;
            centerY = yaw;
            //indicate soundbox off on screen
            this.startTime = System.currentTimeMillis();
        } else if (yaw >= centerY - 4 && isLeftPlayed) {
            isLeftPlayed = false;
            isDownPlayed = false;
            isUpPlayed = false;
            isRightPlayed = false;
            mDirectionView.setText("Center");
            centerP = pitch;
            centerY = yaw;
            //indicate soundbox off on screen
            this.startTime = System.currentTimeMillis();
        } else if (yaw <= centerY + 4 && isRightPlayed) {
            isRightPlayed = false;
            isDownPlayed = false;
            isUpPlayed = false;
            isLeftPlayed = false;
            mDirectionView.setText("Center");
            //indicate soundbox off on screen
            centerP = pitch;
            centerY = yaw;
            this.startTime = System.currentTimeMillis();
        }
    }

    private float velocity(double distance) {
        long endTime = System.currentTimeMillis();
        long deltaTime = (endTime - this.startTime);
        //this.startTime = endTime;
        float vel = (float) (distance / deltaTime);
        vel = (float) (1 / (1 + Math.exp(-1 * vel)));
        vel = (float) (((10.0 * vel) - 5.0) * 1.50);
        if (vel >= 1.0) { return (float) 1.0; }
        else if (vel <= 0.0) { return (float) 0.0; }
        else { return vel; }
    }
    private void onCalibrateClicked() {
        mViewModel.resetInitialReading();
        centerP = 0;
        centerY = 0;
        this.startTime = System.currentTimeMillis();
    }

    private void onSearchClicked() {
        final Intent intent = DeviceConnectorActivity.newIntent(requireContext(), 0,
                SensorViewModel.sensorIntent(SensorType.ROTATION_VECTOR), SensorViewModel.gestureIntent());

        startActivityForResult(intent, 1);
    }

    public void showTutorial() {
    /*
        ConstraintLayout tutorialLayout = (ConstraintLayout) view.findViewById(R.id.tutorialLayout);
        ImageButton tutorialExitBtn;
        ImageButton rightArrowBtn;
        ImageButton leftArrowBtn;
        TextView tutorialTxt;
        TextView tutorialTitleTxt;
        TextView tutorialNumTxt;
        ImageView tutorialImg;

        if(tutorialStep == 1) {

        } else if(tutorialStep == 2) {

        }
        ImageButton databaseBtn = findViewById(R.id.databaseBtn);
        private ConstraintLayout tutorialLayout;
        private ImageButton tutorialExitBtn;
        private ImageButton rightArrowBtn;
        private ImageButton leftArrowBtn;
        private TextView tutorialTxt;
        private TextView tutorialTitleTxt;
        private TextView tutorialNumTxt;
        private ImageView tutorialImg;*/
    }

    public void disableButtons(View view) {

        view.findViewById(R.id.tutorialBtn).setEnabled(false);
        view.findViewById(R.id.connectBtn).setEnabled(false);
        view.findViewById(R.id.calibrateBtn).setEnabled(false);
        view.findViewById(R.id.upBtn).setEnabled(false);
        view.findViewById(R.id.downBtn).setEnabled(false);
        view.findViewById(R.id.leftBtn).setEnabled(false);
        view.findViewById(R.id.rightBtn).setEnabled(false);

    }

    public void enableButtons(View view) {

        view.findViewById(R.id.tutorialBtn).setEnabled(true);
        view.findViewById(R.id.connectBtn).setEnabled(true);
        view.findViewById(R.id.calibrateBtn).setEnabled(true);
        view.findViewById(R.id.upBtn).setEnabled(true);
        view.findViewById(R.id.downBtn).setEnabled(true);
        view.findViewById(R.id.leftBtn).setEnabled(true);
        view.findViewById(R.id.rightBtn).setEnabled(true);

    }
}
