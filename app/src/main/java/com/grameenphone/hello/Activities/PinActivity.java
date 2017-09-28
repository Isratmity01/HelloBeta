package com.grameenphone.hello.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.grameenphone.hello.R;



public class PinActivity extends AppCompatActivity {

    private EditText pincode;
    private Button submitpin;
    private TextView weburl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_pin);

        //actionbar.setHomeAsUpIndicator ( R.drawable.ic_action_keyboard );

        // Back.setVisibility(View.GONE);

        pincode = (EditText) findViewById(R.id.txt_pin_entry);

        submitpin = (Button) findViewById(R.id.submit_pinButton);

        weburl=(TextView)findViewById(R.id.helloweb);
        weburl.setClickable(true);
        weburl.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://www.hellomessages.com/'> সিক্রেট পিনের জন্য এখানে ক্লিক করুন </a>";
        weburl.setText(Html.fromHtml(text));
        submitpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=pincode.getText().toString().trim();
                if(code.equals("43556"))
                {
                    saveInfo(code);
                }
               else Toast.makeText(PinActivity.this,"Please enter hello pin",Toast.LENGTH_SHORT).show();
            }
        });

    }




    @Override
    public void onBackPressed() {

        finish();

    }

    public void saveInfo(String code) {
        pincode.getText().clear();
        Toast.makeText(PinActivity.this,"হ্যালো বেটা-তে স্বাগতম ",Toast.LENGTH_SHORT).show();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PinActivity.this);
        prefs.edit().putBoolean("locked", true).apply();
        startActivity();
    }


    private void startActivity()
    {
        Intent i = new Intent(this,SignInActivity.class);
        startActivity(i);
    }

}
