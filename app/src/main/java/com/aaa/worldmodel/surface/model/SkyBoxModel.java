package com.aaa.worldmodel.surface.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import com.aaa.worldmodel.utils.ShaderUtil;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SkyBoxModel extends Model {
    float skyboxVertices[] = {
            // positions
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,

            -1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,

            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,

            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f
    };
    private int[] textureID = new int[1];
    private int[] vao = new int[1];
    private int LOCATION_POSITION;
    private int LOCATION_SAMPLERCUBE;
    private int LOCATION_MODEL_MATRIX;
    private int LOCATION_VIEW_MATRIX;
    private int LOCATION_PROJECTION_MATRIX;

    public SkyBoxModel(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreate(Context context) {
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("shader/skybox.vert", context.getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("shader/skybox.frag", context.getResources());
        programId=ShaderUtil.createProgram(vertexShaderCode, fragmentShaderCode);

        initLocation();
        initTexture();
        initVAO();
    }

    @Override
    public void onDraw() {
        GLES30.glUseProgram(programId);

        GLES30.glUniform1f(LOCATION_SAMPLERCUBE,0);
        GLES30.glUniformMatrix4fv(LOCATION_MODEL_MATRIX, 1, false, mMatrix, 0);
        GLES30.glUniformMatrix4fv(LOCATION_VIEW_MATRIX, 1, false, vMatrix, 0);
        GLES30.glUniformMatrix4fv(LOCATION_PROJECTION_MATRIX, 1, false, pMatrix, 0);
//        drawNormaly();
        drawVAO();
    }

    @Override
    public void onSurfaceChange(int width, int height) {

    }
    private void drawNormaly(){
        //关闭深度写入。这样天空盒才能成为所有其他物体的背景来绘制出来。
        GLES30.glDepthMask(false);
        Buffer floatBuffer = ByteBuffer.allocateDirect(skyboxVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(skyboxVertices).flip();
        GLES30.glVertexAttribPointer(LOCATION_POSITION, 3, GLES30.GL_FLOAT, false, 3 * 4, floatBuffer);
        GLES30.glEnableVertexAttribArray(LOCATION_POSITION);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        GLES30.glDepthMask(true);
    }

    private void drawVAO(){
        //关闭深度写入。这样天空盒才能成为所有其他物体的背景来绘制出来。
        GLES30.glDepthMask(false);
        GLES30.glBindVertexArray(vao[0]);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, textureID[0]);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        GLES30.glBindVertexArray(0);
        GLES30.glDepthMask(true);
    }
    private void initVAO() {
        int[] vbo = new int[1];
        GLES30.glGenVertexArrays(1, vao, 0);
        GLES30.glGenBuffers(1, vbo, 0);
        GLES30.glBindVertexArray(vao[0]);

        Buffer floatBuffer = ByteBuffer.allocateDirect(skyboxVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(skyboxVertices).flip();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, floatBuffer.capacity() * 4, floatBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(LOCATION_POSITION, 3, GLES30.GL_FLOAT, false, 3 * 4, 0);
        GLES30.glEnableVertexAttribArray(LOCATION_POSITION);

        GLES30.glBindVertexArray(0);

    }

    private void initTexture() {
        GLES30.glGenTextures(1, textureID, 0);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, textureID[0]);
        try {

//            Bitmap front = BitmapFactory.decodeStream(context.getAssets().open("skybox/star1/star_" + "front" + ".jpg"));
//            Bitmap back = BitmapFactory.decodeStream(context.getAssets().open("skybox/star1/star_" + "back" + ".jpg"));
//            Bitmap left = BitmapFactory.decodeStream(context.getAssets().open("skybox/star1/star_" + "left" + ".jpg"));
//            Bitmap right = BitmapFactory.decodeStream(context.getAssets().open("skybox/star1/star_" + "right" + ".jpg"));
//            Bitmap top = BitmapFactory.decodeStream(context.getAssets().open("skybox/star1/star_" + "top" + ".jpg"));
//            Bitmap bottom = BitmapFactory.decodeStream(context.getAssets().open("skybox/star1/star_" + "bottom" + ".jpg"));
//
            Bitmap front = BitmapFactory.decodeStream(context.getAssets().open("skybox/skybox_" + "front" + ".jpg"));
            Bitmap back = BitmapFactory.decodeStream(context.getAssets().open("skybox/skybox_" + "back" + ".jpg"));
            Bitmap left = BitmapFactory.decodeStream(context.getAssets().open("skybox/skybox_" + "left" + ".jpg"));
            Bitmap right = BitmapFactory.decodeStream(context.getAssets().open("skybox/skybox_" + "right" + ".jpg"));
            Bitmap top = BitmapFactory.decodeStream(context.getAssets().open("skybox/skybox_" + "top" + ".jpg"));
            Bitmap bottom = BitmapFactory.decodeStream(context.getAssets().open("skybox/skybox_" + "bottom" + ".jpg"));
//
            GLUtils.texImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, right, 0);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, left, 0);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, front, 0);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, back, 0);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, top, 0);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, bottom, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_R, GLES30.GL_CLAMP_TO_EDGE);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, 0);
    }


    private void initLocation() {
        LOCATION_POSITION = GLES30.glGetAttribLocation(programId, "position");
        LOCATION_SAMPLERCUBE = GLES30.glGetUniformLocation(programId, "skybox");
        LOCATION_MODEL_MATRIX = GLES30.glGetUniformLocation(programId, "model");
        LOCATION_VIEW_MATRIX = GLES30.glGetUniformLocation(programId, "view");
        LOCATION_PROJECTION_MATRIX = GLES30.glGetUniformLocation(programId, "projection");
    }

}
