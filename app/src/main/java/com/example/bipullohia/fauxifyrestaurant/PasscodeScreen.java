package com.example.bipullohia.fauxifyrestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Bipul Lohia on 9/27/2016.
 */

public class PasscodeScreen extends AppCompatActivity {

    EditText restaurantId;
    Button enterButton;
    static String resId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcodescreen);

        restaurantId = (EditText) findViewById(R.id.restaurantid);
        enterButton = (Button) findViewById(R.id.enterbutton);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resId = restaurantId.getText().toString();
                Log.e("respascde44", resId);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }
}
