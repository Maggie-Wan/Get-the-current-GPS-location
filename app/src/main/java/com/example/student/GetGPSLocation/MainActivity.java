package com.example.student.GetGPSLocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private String[] mPermissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100; //permission RequestCode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertPermissions(this);

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null)
        {
// 取到手機地點後的程式碼
            Toast.makeText(this,"GPS Location:" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude(),Toast.LENGTH_SHORT).show();
//            Log.d("GPS", "Location:" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //***************************權限相關***********************************

    //判斷sdk版本是否會彈跳出權限對話框
    public boolean isPoupUpPermissions() {
        //Build.VERSION.SDK_INT 取得目前sdk版本
        //android 6.0=23 因為23以上才會跳出權限對話框
        return Build.VERSION.SDK_INT >= 23;
    }

    //判斷是否已經有權限
    public boolean hasPermissions(Context context, String permission) {
        if (!isPoupUpPermissions()) { //sdk 23以下當作已經有取得權限
            return true;
        }

        //確認權限是否為PERMISSION_GRANTED
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        buildGoogleApiClient();
        return true;
    }

    //請求權限(已擁有權限－PERMISSION_GRANTED ,無權限－PERMISSION_DENIED)
    private void alertPermissions(Activity context) {
        final ArrayList<String> unsatisfiedPermissions = new ArrayList<>(); //用來儲存未取得的權限
        for (String permission : mPermissions) {
            if (!hasPermissions(context, permission)) {
                //如果某個permission檢查後為PackageManager.PERMISSION_DENIED，就加到unsatisfiedPermissions arrayListy中
                unsatisfiedPermissions.add(permission);
            }
        }
        //將unsatisfiedPermissions 轉成 string array，並且對array中的每個permission做request
        if (unsatisfiedPermissions.size() != 0) {//當所有權限皆允許時unsatisfiedPermissions.size()=0 ，此時不需要requestPermissions
            ActivityCompat.requestPermissions(context, unsatisfiedPermissions.toArray(new String[unsatisfiedPermissions.size()]), MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }
}
