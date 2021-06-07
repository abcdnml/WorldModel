package com.aaa.worldmodel.surface;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Half;
import android.view.MotionEvent;

import java.util.logging.Handler;

public class WorldSurfaceView extends GLSurfaceView  {
    private WorldRender renderer;
    private TouchHandler touchHandler;

    public WorldSurfaceView(Context context) {
        super(context);
        init();
    }

    public WorldSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(3);
        renderer = new WorldRender(this,Color.argb(1,33,162,254));
        setRenderer(renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
//        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        touchHandler=new TouchHandler(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchHandler.onTouchEvent(event);
    }


    public WorldRender getRenderer() {
        return renderer;
    }

}
