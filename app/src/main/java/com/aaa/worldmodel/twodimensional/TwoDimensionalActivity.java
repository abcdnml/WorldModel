package com.aaa.worldmodel.twodimensional;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;


public class TwoDimensionalActivity extends Activity {

    public static final String TAG = "TwoDimensionalActivity";

    TwoDimensionalView mview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mview = new TwoDimensionalView(this);
        mview.requestFocus();
        mview.setFocusableInTouchMode(true);
        setContentView(mview);

    }


    @Override
    public void onResume() {
        super.onResume();
        mview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
