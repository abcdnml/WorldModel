package com.aaa.worldmodel.surface.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.aaa.worldmodel.surface.obj.Obj3D;
import com.aaa.worldmodel.utils.ShaderUtil;
import com.aaa.worldmodel.utils.LogUtils;
import com.google.gson.Gson;

import java.io.IOException;

public class Obj3DShape1 extends Model {
    private static final int LOCATION_POSITION = 0;
    private static final int LOCATION_COORDINATE = 1;
    private static final int LOCATION_NORMAL = 2;
    private static final int LOCATION_MATRIX = 3;
    private static final int LOCATION_KA = 4;
    private static final int LOCATION_KD = 5;
    private static final int LOCATION_KS = 6;
    private static final int LOCATION_TEXTURE = 7;
    int r = 3;
    float angle = 0;
    float aspectRatio = 1;
    float scale = 1f;
    private float[] mMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private int textureId;
    private Obj3D obj3D;
    private Bitmap bitmap;

    public Obj3DShape1(Context context, Obj3D obj3D,float scale) {
        super(context);
        this.obj3D=obj3D;
        this.scale=scale;

        LogUtils.i("obj: "+new Gson().toJson(obj3D));
        try {
            bitmap=BitmapFactory.decodeStream(context.getAssets().open("obj/" + obj3D.mtl.map_Kd));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMatrix(float[] mMatrix, float[] vMatrix,float[] pMatrix) {

    }

    @Override
    public void onSurfaceCreate(Context context) {
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("shader/obj2.vert", context.getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("shader/obj2.frag", context.getResources());
        programId = ShaderUtil.createProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void onDraw() {
        GLES30.glUseProgram(programId);
        Matrix.rotateM(mMatrix,0,0.3f,0,1,0);

        GLES30.glUniformMatrix4fv(LOCATION_MATRIX, 1, false, mMatrix, 0);

        GLES30.glEnableVertexAttribArray(LOCATION_POSITION);
        GLES30.glVertexAttribPointer(LOCATION_POSITION, 3, GLES30.GL_FLOAT, false, 0, obj3D.vert);
        if(obj3D.vertTexture!=null){
            GLES30.glEnableVertexAttribArray(LOCATION_COORDINATE);
            GLES30.glVertexAttribPointer(LOCATION_COORDINATE, 2, GLES30.GL_FLOAT, false, 0, obj3D.vertTexture);
        }
        GLES30.glEnableVertexAttribArray(LOCATION_NORMAL);
        GLES30.glVertexAttribPointer(LOCATION_NORMAL, 3, GLES30.GL_FLOAT, false, 0, obj3D.vertNorl);
//
        GLES30.glUniform3fv(LOCATION_KA, 1, obj3D.mtl.Ka, 0);
        GLES30.glUniform3fv(LOCATION_KD, 1, obj3D.mtl.Kd, 0);
        GLES30.glUniform3fv(LOCATION_KS, 1, obj3D.mtl.Ks, 0);
//
        if(bitmap!=null){
            textureId = createTexture(bitmap);
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
            GLES30.glUniform1i(LOCATION_TEXTURE, 0);
        }

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, obj3D.vertCount);

        GLES30.glDisableVertexAttribArray(LOCATION_POSITION);
        GLES30.glDisableVertexAttribArray(LOCATION_NORMAL);
        GLES30.glDisableVertexAttribArray(LOCATION_COORDINATE);
    }

    @Override
    public void onSurfaceChange(int width, int height) {
        aspectRatio = (width + 0f) / height;
        //调用此方法计算产生透视投影矩阵
        float[] matrix= getOriginalMatrix();
        Matrix.scaleM(matrix,0,scale,scale*width/height,scale);
        Matrix.setLookAtM(mVMatrix, 0, 3,3 ,3 , 0f, 0f, 0f, 0f, 0f, 1.0f);
        Matrix.multiplyMM(mMatrix, 0, matrix, 0, mVMatrix, 0);
    }

    private int createTexture(Bitmap bitmap) {
        int[] texture = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            //生成纹理
            GLES30.glGenTextures(1, texture, 0);
            //生成纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return 0;
    }
}
