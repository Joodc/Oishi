package com.example.oishi.map;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.oishi.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public abstract class CurrentLocationActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    ImageButton current_location_Button;
    TextView address_Text, address_type_Text;
    Button setting_location_Button;

    private GoogleMap mMap;
    private Marker currentMarker = null;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;

    public static final double CAMERA_LATITUDE_BASE = 37.394526;
    public static final double CAMERA_LONGITUDE_BASE = 126.943164;
    public static final int CAMERA_LEVEL = 18;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private View mLayout;

    Location mCurrentLocation;//사용자 위치 주소 객체
    String mCurrentAddress;//사용자 위치 주소값
    String mCurrentCoordinate;//사용자 위치 좌표값
    LatLng currentPosition;//사용자 위치 좌표 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mLayout = findViewById(R.id.activity_current_location);

        //위치 서비스 클라이언트 만들기
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //마지막으로 알려진 위치 가져오기
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                            String markerTitle = getCurrentAddress(currentPosition);
                            String markerSnippet = " 위도 : " + location.getLatitude()
                                    + " 경도 : " + location.getLongitude();

                            mCurrentLocation = location;
                            mCurrentAddress = markerTitle;
                            mCurrentCoordinate = markerSnippet;

                            //현재 위치에 마커 생성
                            setCurrentLocation(location);

                            address_Text.setText(mCurrentAddress);
                            address_type_Text.setText(R.string.address_normal_type);
                        }
                    }
                });

        createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        //위치 업데이트 콜백 정의
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                    String markerTitle = getCurrentAddress(currentPosition);
                    String markerSnippet = " 위도 : " + location.getLatitude()
                            + " 경도 : " + location.getLongitude();

                    mCurrentLocation = location;
                    mCurrentAddress = markerTitle;
                    mCurrentCoordinate = markerSnippet;

                    //현재 위치에 마커 생성
                    setCurrentLocation(location);
                }
            }
        };

        //SupportMapFragment 설정 단계
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            //이 클래스는 지도 시스템과 뷰를 자동으로 초기화한다.
            mapFragment.getMapAsync(this);
        }

        current_location_Button = findViewById(R.id.current_location_Button);
        address_Text = findViewById(R.id.address_Text);
        address_type_Text = findViewById(R.id.address_type_Text);
        setting_location_Button = findViewById(R.id.setting_location_Button);

        current_location_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                String txtAddressType = address_type_Text.getText().toString();
                if(txtAddressType.equals(getString(R.string.address_normal_type))) {
                    address_Text.setText(mCurrentAddress);
                }
                else if(txtAddressType.equals(getString(R.string.address_road_type))) {
                    address_Text.setText(mCurrentCoordinate);
                }

                 */
            }
        });

        address_type_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                String txtAddressType = address_type_Text.getText().toString();
                if(txtAddressType.equals(getString(R.string.address_normal_type))) {
                    address_Text.setText(mCurrentCoordinate);
                    address_type_Text.setText(R.string.address_road_type);
                }
                else if(txtAddressType.equals(getString(R.string.address_road_type))) {
                    address_Text.setText(mCurrentAddress);
                    address_type_Text.setText(R.string.address_normal_type);
                }

                 */
            }
        });

        setting_location_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(CurrentLocationActivity.this, SearchAddressActivity.class);
                //해당 위치에 마커가 박힌 구글맵 보내주기
                intent.putExtra("address_normal_type", mCurrentCoordinate);
                intent.putExtra("address_road_type", mCurrentAddress);
                startActivity(intent);
                finish();

                 */
            }
        });
    }

    //LocationRequest의 용도
    //필요한 수준의 정확도/전력 소비 및 원하는 업데이트 간격을 지정하고 기기는 시스템 설정을 자동으로 적절하게 변경
    protected void createLocationRequest() {
        locationRequest = LocationRequest.create()
                //앱에서 선호하는 위치 업데이트 수신 속도(밀리초)를 설정
                .setInterval(UPDATE_INTERVAL_MS)
                //앱에서 위치 업데이트를 처리할 수 있는 가장 빠른 속도(밀리초)를 설정
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS)
                // 요청 우선순위를 설정(Google Play 서비스 위치 서비스에 사용할 위치 소스에 관한 강력한 힌트를 제공)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public String getCurrentAddress(LatLng latlng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        }
        catch (IOException ioException) {
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        }
        catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        else {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        }
    }

    public void setCurrentLocation(Location location) {
        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.draggable(false);

        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, CAMERA_LEVEL);
        mMap.moveCamera(cameraUpdate);
    }

    //지도를 사용할 준비가 되었을 때의 콜백 인터페이스
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        setDefaultLocation();

        if (checkPermission()) {
            startLocationUpdates();
        }

        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(CurrentLocationActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                            }
                        })
                        .show();
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(CurrentLocationActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                            }
                        })
                        .show();
            }

            else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        mMap.setMaxZoomPreference(19);
        mMap.setMinZoomPreference(7);
    }

    public void setDefaultLocation() {

        LatLng mapPosition = new LatLng(CAMERA_LATITUDE_BASE, CAMERA_LONGITUDE_BASE);

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions()
                .position(mapPosition)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mapPosition, CAMERA_LEVEL);
        mMap.moveCamera(cameraUpdate);
    }

    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[0]);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[1]);

        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationUpdates() {

        if (checkLocationServicesStatus()) {

            if (!checkPermission()) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    public boolean checkLocationServicesStatus() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null) {

            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CurrentLocationActivity.this);
                builder.setMessage("위치 설정을 허용해주세요");
                builder.setCancelable(true);

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("설정하러 가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent
                                = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                    }
                });

                builder.create().show();
                return false;
            }

            else if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CurrentLocationActivity.this);
                builder.setMessage("네트워크가 연결되지 않았습니다.\n" + "Wi-Fi 또는 데이터를 활성화 해주세요.");
                builder.setCancelable(true);

                builder.setNegativeButton("다시 시도", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                builder.create().show();
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (checkPermission()) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                startLocationUpdates();
            }

            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            })
                            .show();
                }

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            })
                            .show();
                }

                else {
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            })
                            .show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_ENABLE_REQUEST_CODE) {
            if (checkLocationServicesStatus()) {
                needRequest = true;
            }
        }
    }
}