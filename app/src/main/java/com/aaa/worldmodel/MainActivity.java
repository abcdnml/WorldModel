package com.aaa.worldmodel;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLES30;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.aaa.worldmodel.surface.WorldRender;
import com.aaa.worldmodel.surface.WorldSurfaceView;
import com.aaa.worldmodel.surface.shape.Triangle;

public class MainActivity extends AppCompatActivity {


    private WorldSurfaceView worldSurfaceView;
    private WorldRender worldRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ((WorldSurfaceView)findViewById(R.id.sv_world)).setEGLContextClientVersion(3);
        DisplayMetrics metrics = new DisplayMetrics();
        getDisplay().getRealMetrics(metrics);
        Log.i("aaaaaaaaaa", " metrics.densityDpi " + metrics.densityDpi + "density : " + metrics.density);
        worldSurfaceView = findViewById(R.id.sv_world);
        worldRender = worldSurfaceView.getRenderer();
    }

}
