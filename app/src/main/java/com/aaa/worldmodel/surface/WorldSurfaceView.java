package com.aaa.worldmodel.surface;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class WorldSurfaceView extends GLSurfaceView {
    private WorldRender renderer;

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
        renderer = new WorldRender(this,Color.WHITE);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public WorldRender getRenderer() {
        return renderer;
    }

}
