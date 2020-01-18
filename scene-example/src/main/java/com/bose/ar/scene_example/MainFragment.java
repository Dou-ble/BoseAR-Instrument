package com.bose.ar.scene_example;

//
//  MainFragment.java
//  BoseWearable
//
//  Created by Tambet Ingo on 11/14/2018.
//  Copyright © 2018 Bose Corporation. All rights reserved.
//

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bose.scene_example.R;
import com.bose.wearable.sensordata.QuaternionAccuracy;
import com.bose.wearable.services.wearablesensor.ProductInfo;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private SensorViewModel mViewModel;

    private Node mProductNode;

    private boolean isLeftPlayed;
    private boolean isRightPlayed;
    private boolean isUpPlayed;
    private boolean isDownPlayed;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        mSceneView = view.findViewById(R.id.scene_view);
        mValuesView = view.findViewById(R.id.valuesText);
        mAccuracyView = view.findViewById(R.id.accuracyText);
        mDirectionView = view.findViewById(R.id.directionText);

        //adds listener for calibrate button - goes to promotion screen
        ImageButton calibrateBtn = view.findViewById(R.id.calibrateBtn);
        calibrateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Calibrate");
                mViewModel.resetInitialReading();
            }
        });

        /*
        //adds listener for connect button - goes to promotion screen
        ImageButton connectBtn = view.findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Connect");

                mViewModel.onSearchClicked();
            }
        }); */

        

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

        mViewModel.sensorData()
            .observe(this, this::updatePosition);

        mViewModel.sensorAccuracy()
            .observe(this, this::updateAccuracy);

        readPrefs(PreferenceManager.getDefaultSharedPreferences(requireContext()));
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
        // these if statements play sound if head reaches certain positions
        if (pitch <= -15 && !isDownPlayed) {
            isDownPlayed = true;
            mDirectionView.setText("Down");
            //play sound and indicate on screen that down played
        } else if (pitch >= 15 && !isUpPlayed) {
            isUpPlayed = true;
            mDirectionView.setText("Up");
            //play up sound and indicate on screen
        } else if (yaw <= -15 && !isLeftPlayed) {
            isLeftPlayed = true;
            mDirectionView.setText("Left");
            //play left sound and indicate on screen
        } else if (yaw >= 15 && !isRightPlayed) {
            isRightPlayed = true;
            mDirectionView.setText("Right");
            // play right sound and indicate on screen
        }

        // these if statements reset soundboxes if head reaches certain positions
        if (pitch >= -10 && isDownPlayed) {
            isDownPlayed = false;
            mDirectionView.setText("Center");
            //indicate soundbox off on screen
        } else if (pitch <= 10 && isUpPlayed) {
            isUpPlayed = false;
            mDirectionView.setText("Center");
            //indicate soundbox off on screen
        } else if (yaw >= -10 && isLeftPlayed) {
            isLeftPlayed = false;
            mDirectionView.setText("Center");
            //indicate soundbox off on screen
        } else if (yaw <= 10 && isRightPlayed) {
            isRightPlayed = false;
            mDirectionView.setText("Center");
            //indicate soundbox off on screen
        }
    }


}
