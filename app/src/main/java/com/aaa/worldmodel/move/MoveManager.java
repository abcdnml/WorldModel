package com.aaa.worldmodel.move;

import android.opengl.Matrix;

import com.aaa.worldmodel.utils.LogUtils;

public class MoveManager {

    MovementListener movementListener = new MovementListener() {
        @Override
        public void onMove(float positionX, float positionY, float positionZ, float directionX, float directionY, float directionZ) {
            LogUtils.i("position: (" + positionX + "," + positionY + "," + positionZ + ")");
            LogUtils.i("direction: (" + directionX + "," + directionY + "," + directionZ + ")");
        }
    };
    /**
     * 眼睛位置
     */
    private float[] position = new float[]{0, 0, 0.5f};
    /**
     * 视线方向
     */
    private float[] direction = new float[]{0, 0, -1};
    /**
     * 法线方向 , 比如人站立时的上方
     */
    private float[] normal = new float[]{0, 1, 0};

    public float[] getPosition() {
        return position;
    }

    private float[] oula=new float[3];

    /**
     * 获取视图矩阵
     *
     * @param vMatrix 视图矩阵
     * @return
     */
    public float[] getViewMatrix(float[] vMatrix) {
        // 目标中心点的位置等于 位置+方向
        float centerX = direction[0] + position[0];
        float centerY = direction[1] + position[1];
        float centerZ = direction[2] + position[2];
        Matrix.setLookAtM(vMatrix, 0, position[0], position[1], position[2], centerX, centerY, centerZ, normal[0], normal[1], normal[2]);
        return vMatrix;
    }

    public float[] move(float distanceX, float distanceY, float distanceZ) {
        position[0] = position[0] + distanceX;
        position[1] = position[1] + distanceY;
        position[2] = position[2] + distanceZ;

        // 目标中心点的位置等于 位置+方向
        float centerX = direction[0] + position[0];
        float centerY = direction[1] + position[1];
        float centerZ = direction[2] + position[2];

        float[] vMatrix = new float[16];
        Matrix.setLookAtM(vMatrix, 0, position[0], position[1], position[2], centerX, centerY, centerZ, normal[0], normal[1], normal[2]);

        return vMatrix;
    }

    public float[] moveTo(float x, float y, float z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;

        // 中心点的位置等于 位置+方向
        float centerX = direction[0] + position[0];
        float centerY = direction[1] + position[1];
        float centerZ = direction[2] + position[2];

        float[] vMatrix = new float[16];
        Matrix.setLookAtM(vMatrix, 0, position[0], position[1], position[2], centerX, centerY, centerZ, normal[0], normal[1], normal[2]);
        return vMatrix;

    }

    /**
     * 旋转视角
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public float[] rotate(float pitch, float yaw, float roll) {

        if(pitch>89){
            pitch=89;
        }
        if(pitch<-89){
            pitch=-89;
        }
        oula[0]+=pitch;
        oula[1]+=yaw;
        oula[2]+=roll;

        direction[0] += (float) Math.cos( Math.toRadians(oula[0])) *  (float)Math.cos( Math.toRadians(oula[1]));
        direction[1] += (float)Math.sin( Math.toRadians(oula[0]));
        direction[2] += (float)Math.cos( Math.toRadians(oula[0])) *  (float)Math.sin( Math.toRadians(oula[1]));

        // 中心点的位置等于 位置+方向
        float centerX = direction[0] + position[0];
        float centerY = direction[1] + position[1];
        float centerZ = direction[2] + position[2];

        float[] vMatrix = new float[16];
        Matrix.setLookAtM(vMatrix, 0, position[0], position[1], position[2], centerX, centerY, centerZ, normal[0], normal[1], normal[2]);
        return vMatrix;
    }

    public float[] rotateTo(float pitch, float yaw) {
        direction[0] = (float) Math.cos( Math.toRadians(pitch)) *  (float)Math.cos( Math.toRadians(yaw));
        direction[1] = (float)Math.sin( Math.toRadians(pitch));
        direction[2] = (float)Math.cos( Math.toRadians(pitch)) *  (float)Math.sin( Math.toRadians(yaw));

        // 中心点的位置等于 位置+方向
        float centerX = direction[0] + position[0];
        float centerY = direction[1] + position[1];
        float centerZ = direction[2] + position[2];

        float[] vMatrix = new float[16];
        Matrix.setLookAtM(vMatrix, 0, position[0], position[1], position[2], centerX, centerY, centerZ, normal[0], normal[1], normal[2]);
        return vMatrix;
    }

    private void calculateNormalDirection() {

    }


}
