package com.example.oishi.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oishi.R;

public class SettingAddressActivity extends AppCompatActivity {

    EditText search_address_Text;
    ImageButton search_address_Button;
    Button current_location_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_address);

        search_address_Text = findViewById(R.id.search_address_Text);
        search_address_Button = findViewById(R.id.search_address_Button);
        current_location_Button = findViewById(R.id.current_location_Button);

        search_address_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                String txtAddress = search_address_Text.getText().toString();
                if (TextUtils.isEmpty(txtAddress)) {
                    Toast.makeText(SettingAddressActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), SearchAddressActivity.class);
                    intent.putExtra("address", txtAddress);
                    SettingAddressActivity.this.startActivity(intent);
                }
                */
            }
        });
        current_location_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CurrentLocationActivity.class);
                startActivity(intent);
            }
        });
    }
}