package com.example.fosterw1.finalprojectcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Willem on 12/7/2015.
 */
public class MenuActivity extends AppCompatActivity implements Button.OnClickListener {
    private Button button;
    private Button button2;
    private Button button3;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2015-12-07 19:22:16 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        button = (Button)findViewById( R.id.button );
        button2 = (Button)findViewById( R.id.button2 );
        button3 = (Button)findViewById( R.id.button3 );

        button.setOnClickListener( this );
        button2.setOnClickListener( this );
        button3.setOnClickListener( this );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        findViews();

    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2015-12-07 19:22:16 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == button ) {
            Intent intent = new Intent(MenuActivity.this, GoldCoinActivity.class);
            startActivity(intent);
        } else if ( v == button2 ) {
            Intent intent = new Intent(MenuActivity.this, IronOreActivity.class);
            startActivity(intent);
        } else if ( v == button3 ) {
            Intent intent = new Intent(MenuActivity.this, SteelActivity.class);
            startActivity(intent);
        }
    }


}
