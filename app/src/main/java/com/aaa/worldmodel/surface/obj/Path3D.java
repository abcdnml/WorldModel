package com.aaa.worldmodel.surface.obj;

import com.aaa.worldmodel.utils.LogUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Path3D {
    public FloatBuffer vert;
    public int vertCount;

    public void setVert(ArrayList<Float> data){
        int size=data.size();
        vert= ByteBuffer.allocateDirect(size*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for (int i=0;i<size;i++){
            vert.put(data.get(i));
        }
        vert.flip();
        vertCount=size/3;
        LogUtils.i("vertCount: "+ vertCount);
    }

    public void setVert(FloatBuffer buffer){
        vert=buffer;
        vertCount=vert.capacity()/3;
        LogUtils.i("vertCount: "+ vertCount);
    }

}
