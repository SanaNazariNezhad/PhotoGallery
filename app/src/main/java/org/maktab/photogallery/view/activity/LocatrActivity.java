package org.maktab.photogallery.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.maktab.photogallery.R;
import org.maktab.photogallery.view.fragment.LocatrFragment;

public class LocatrActivity extends SingleFragmentActivity {
    private static Context sContext;

    private static final int REQUEST_ERROR = 0;

    public static Intent newIntent(Context context) {
        sContext = context;
        return new Intent(context, LocatrActivity.class);
    }

    @Override
    public Fragment createFragment() {
        return LocatrFragment.newInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            //using correct alert dialog.

            /*Dialog errorDialog = apiAvailability.getErrorDialog(this, errorCode, REQUEST_ERROR,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    });
            errorDialog.show();*/
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) &&ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)){

            alertDialog();

        }
    }

    private void alertDialog() {
        String message = getString(R.string.permission) + "\n" + getString(R.string.permission_go_to)
                + "\n" + getString(R.string.path_of_permission);
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(message)

                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}