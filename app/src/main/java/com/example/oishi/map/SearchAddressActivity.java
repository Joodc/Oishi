package com.example.oishi.map;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oishi.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class SearchAddressActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    ImageButton setting_location_Button;
    TextView address_normal_Text, address_road_Text;
    EditText detail_address_Text;
    Button confirm_address_Button;

    private GoogleMap mMap;
    public static final int CAMERA_LEVEL = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //Intent intent = getIntent();

        setting_location_Button = findViewById(R.id.setting_location_Button);
        address_normal_Text = findViewById(R.id.address_normal_Text);
        address_road_Text = findViewById(R.id.address_road_Text);
        detail_address_Text = findViewById(R.id.detail_address_Text);
        confirm_address_Button = findViewById(R.id.confirm_address_Button);

        setting_location_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //지도, 주소, 도로명주소를 CurrentLocationActivity로 전달
                /*
                Intent intent = new Intent(SearchAddressActivity.this, CurrentLocationActivity.class);
                intent.putExtra("map", mMap);
                intent.putExtra("address_normal_type", mCurrentCoordinate);
                intent.putExtra("address_road_type", mCurrentAddress);
                startActivity(intent);
                finish();

                 */
            }
        });

        confirm_address_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                String txtDetail = detail_address_Text.getText().toString();
                String txtAddress = intent.getExtras().getString("address_road_type");
                String txtCoordinate = intent.getExtras().getString("address_normal_type");
                String UserAddress = txtAddress + " " + txtDetail;
                String UserCoordinate = txtCoordinate + " " + txtDetail;

                Intent intent = new Intent(SearchAddressActivity.this, HomeFragment.class);
                intent.putExtra("UserAddress", UserAddress);
                intent.putExtra("UserCoordinate", UserCoordinate);
                startActivity(intent);
                finish();

                 */
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        /*
        LatLng mapPosition = new LatLng(CAMERA_LATITUDE_BASE, CAMERA_LONGITUDE_BASE);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(mapPosition)
                .title("배달받는곳")
                .draggable(false);

        mMap.addMarker(markerOptions);

        CameraPosition mCameraPosition = new CameraPosition.Builder()
                .target(mapPosition)
                .zoom(CAMERA_LEVEL)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));

         */
    }
}