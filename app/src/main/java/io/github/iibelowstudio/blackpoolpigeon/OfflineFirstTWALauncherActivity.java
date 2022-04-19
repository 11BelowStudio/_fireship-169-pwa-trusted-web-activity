package io.github.iibelowstudio.blackpoolpigeon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.google.androidbrowserhelper.trusted.LauncherActivity;
import com.placeholder.R;
import com.placeholder.databinding.ActivityOfflineFirstTwaBindingImpl;

public class OfflineFirstTWALauncherActivity extends LauncherActivity implements ImPressable {
    @Override
    protected boolean shouldLaunchImmediately() {
        // launchImmediately() returns `false` so we can check connection
        // and then render a fallback page or launch the Trusted Web Activity with `launchTwa()`.
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tryLaunchTwa();
    }

    private void tryLaunchTwa() {
        // If TWA has already launched successfully, launch TWA immediately.
        // Otherwise, check connection status. If online, launch the Trusted Web Activity with `launchTwa()`.
        // Otherwise, if offline, render the offline fallback screen.
        if (hasTwaLaunchedSuccessfully()) {
            launchTwa();
        } else if (isOnline()) {
            firstTimeLaunchTwa();
        } else {
            renderOfflineFallback();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private boolean hasTwaLaunchedSuccessfully() {
        // Return `true` if the preference "twa_launched_successfully" has already been set.
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.twa_offline_first_preferences_file_key), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(getString(R.string.twa_launched_successfully), false);
    }

    private void firstTimeLaunchTwa() {
        // Launch the TWA and set the preference "twa_launched_successfully" to true, to indicate
        // that it has launched successfully, at least, once.
        launchTwa();

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.twa_offline_first_preferences_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.twa_launched_successfully), true);
        editor.apply();
    }



    private void renderOfflineFallback() {


        ActivityOfflineFirstTwaBindingImpl binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_offline_first_twa
        );

        binding.setThePressable(this);

        this.setContentView(binding.getRoot());

        //ContentFirstBinding mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.content_first, (FrameLayout) findViewById(R.id.content_frame), true)
        /*
        Button retryBtn = this.findViewById(R.id.reconnectButton);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check connection status. If online, launch the Trusted Web Activity for the first time.
                if (isOnline()) firstTimeLaunchTwa();
            }
        });

         */
    }


    @Override
    public void infoPress() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                OfflineFirstTWALauncherActivity.this
        );

        builder.setTitle(R.string.app_info_title);
        builder.setMessage(R.string.app_info_body);

        builder.setNeutralButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            @NonNull final DialogInterface theDialog,
                            final int choice
                    ) {
                        theDialog.dismiss();
                    }
                }
        );

        builder.create().show();
    }

    @Override
    public void reconnectPress() {
        if (isOnline()) firstTimeLaunchTwa();
    }
}