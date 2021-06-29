package com.aaa.worldmodel.surface.model;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.aaa.worldmodel.twodimensional.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class Triangle extends Model {

    private static final String TAG = Triangle.class.getSimpleName();

    private static final int LOCATION_VERTEX = 0;
    private static final int LOCATION_COLOR = 1;
    private static final int LOCATION_MATRIX = 2;

    private static final int VERTEX_SIZE = 3;
    private static final int COLOR_SIZE = 3;
    private final float[] mMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private int drawType = GLES30.GL_TRIANGLES;


    public Triangle(Context context , FloatBuffer vertex, FloatBuffer color, int type) {
        super(context);
        vertexBuffer = vertex;
        colorBuffer = color;
        setDrawType(type);
    }

    private Triangle(Builder builder) {
        this(builder.context,builder.vertexBuffer, builder.colorBuffer, builder.drawType);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void setDrawType(int type) {
        if (type == GLES30.GL_TRIANGLES || type == GLES30.GL_TRIANGLE_STRIP || type == GLES30.GL_TRIANGLE_FAN) {
            drawType = type;
        } else {
            Log.e(TAG, "DrawType not correct, should be  GLES30.GL_TRIANGLES ，GLES30.GL_TRIANGLE_STRIP or GLES30.GL_TRIANGLE_FAN！");
        }
    }

    @Override
    public void onSurfaceChange(int width, int height) {
/*        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            //横屏
            Matrix.orthoM(mMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            //竖屏
            Matrix.orthoM(mMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }*/
        final float aspectRatio = (width + 0f) / height;
        //调用此方法计算产生透视投影矩阵
        Matrix.frustumM(mProjMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 1, 10);
        //调用此方法产生摄像机9参数位置矩阵
        Matrix.setLookAtM(mVMatrix, 0, 3, 3, 3, 0f, 0f, 0f, 0f, 0.0f, 1.0f);

        Matrix.multiplyMM(mMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
    }

    @Override
    public void setMatrix(float[] mMatrix, float[] vMatrix,float[] pMatrix) {

    }

    @Override
    public void onSurfaceCreate(Context context) {
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("shader/triangle.vert", context.getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("shader/triangle.frag", context.getResources());
        programId = ShaderUtil.createProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void onDraw() {
        if (programId == 0) {
            Log.e(TAG, "Program id is 0 ,may not init");
            return;
        }
        GLES30.glUseProgram(programId);
        GLES30.glEnableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glEnableVertexAttribArray(LOCATION_COLOR);
        GLES30.glUniformMatrix4fv(LOCATION_MATRIX, 1, false, mMatrix, 0);

        GLES30.glVertexAttribPointer(LOCATION_VERTEX, VERTEX_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * VERTEX_SIZE, vertexBuffer);
        GLES30.glVertexAttribPointer(LOCATION_COLOR, COLOR_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * COLOR_SIZE, colorBuffer);

        GLES30.glDrawArrays(drawType, 0, vertexBuffer.capacity() / VERTEX_SIZE);

        GLES30.glDisableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glDisableVertexAttribArray(LOCATION_COLOR);
    }

    public static final class Builder {
        private FloatBuffer vertexBuffer;
        private FloatBuffer colorBuffer;
        private int drawType;
        private Context context;

        private Builder() {
        }

        public Builder vertexBuffer(float[] vertex) {
            vertexBuffer = ByteBuffer.allocateDirect(vertex.length * FLOAT_SIZE)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer().put(vertex);
            vertexBuffer.flip();
            return this;
        }

        public Builder colorBuffer(float[] color) {
            colorBuffer = ByteBuffer.allocateDirect(color.length * FLOAT_SIZE)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer().put(color);
            colorBuffer.flip();
            return this;
        }

        public Builder vertexBuffer(FloatBuffer vertex) {
            vertexBuffer = vertex;
            return this;
        }

        public Builder colorBuffer(FloatBuffer color) {
            colorBuffer = color;
            return this;
        }

        public Builder drawType(int drawType) {
            this.drawType = drawType;
            return this;
        }
        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Triangle build() {
            if(context==null){
                throw new IllegalArgumentException("context is null! ");
            }
            if (/*vertex.length <= 9 ||*/ vertexBuffer.capacity() % 3 != 0) {
                throw new IllegalArgumentException("vertex length not correct! ");
            }
            if (/*color.length <= 9 ||*/ colorBuffer.capacity() % 3 != 0) {
                throw new IllegalArgumentException("vertex length not correct! ");
            }
            if (drawType != GLES30.GL_TRIANGLES && drawType != GLES30.GL_TRIANGLE_STRIP && drawType != GLES30.GL_TRIANGLE_FAN) {
                throw new IllegalArgumentException("vertex length not correct! ");
            }

            return new Triangle(this);
        }
    }
}
