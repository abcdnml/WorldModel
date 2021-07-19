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
    private float[] direction = new float[]{0, 0, -1}; //如果以上北下南左西右东 逆时针方向为正方向 默认朝向北方
    /**
     * 欧拉角 用于计算视角旋转
     */
    private float[] euler = new float[]{0, -90, 0}; //如果是0 0 0 是面向东边  所以与上面对应 -90度是北方

    /**
     * 法线方向 , 比如人站立时的上方
     */
    private float[] normal = new float[]{0, 1, 0};

    public float[] getPosition() {
        return position;
    }

    /**
     * 获取视图矩阵
     *
     * @return
     */
    public float[] genViewMatrix() {
        float[] vMatrix = new float[16];
        // 眼睛的目标的位置等于 位置+方向
        float centerX = direction[0] + position[0];
        float centerY = direction[1] + position[1];
        float centerZ = direction[2] + position[2];
        Matrix.setLookAtM(vMatrix, 0, position[0], position[1], position[2], centerX, centerY, centerZ, normal[0], normal[1], normal[2]);

        //回调
        if (movementListener != null) {
            movementListener.onMove(position[0], position[1], position[2], direction[0], direction[1], direction[2]);
        }

        return vMatrix;
    }

    /**
     * 沿着当前朝向移动
     *
     * @param distance 距离 或者说 速度
     * @return 视图矩阵
     */
    public float[] moveDirection(float distance) {
        return moveDirection(distance, direction);
    }

    /**
     * 朝着某个方向移动指定距离
     * (比如我可以看着右边但是往前走 这时可以额外指定)
     *
     * @param distance 移动距离
     * @param direct   移动方向
     * @return 视图矩阵
     */
    public float[] moveDirection(float distance, float[] direct) {
        return move(distance * direct[0], distance * direct[1], distance * direct[2]);
    }

    /**
     * 根据向量移动
     *
     * @param distanceX distanceY distanceZ
     * @return 视图矩阵
     */
    public float[] move(float distanceX, float distanceY, float distanceZ) {
        return moveTo(position[0] + distanceX, position[1] + distanceY, position[2] + distanceZ);
    }

    /**
     * 移动到某个坐标
     *
     * @param x y z
     * @return 视图矩阵
     */
    public float[] moveTo(float x, float y, float z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;
        return genViewMatrix();
    }

    /**
     * 旋转视角
     *
     * @param x y z
     * @return
     */
    public float[] rotate(float pitch, float yaw, float roll) {
        euler[0] += pitch;
        euler[1] += yaw;
        euler[2] += roll;
        return rotateTo(euler);
    }


    /**
     * 旋转方向限制
     *
     * @param eulerAngle
     */
    private void rotateLimit(float[] eulerAngle) {
        //限制俯仰角角度  否则超过90度就会翻转
        if (eulerAngle[0] > 89) {
            eulerAngle[0] = 89;
        }
        if (eulerAngle[0] < -89) {
            eulerAngle[0] = -89;
        }
    }

    /**
     * 旋转到指定角度
     *
     * @param eulerAngle 欧拉角
     * @return
     */
    public float[] rotateTo(float[] eulerAngle) {

        rotateLimit(eulerAngle);

        direction[0] = (float) Math.cos(Math.toRadians(eulerAngle[0])) * (float) Math.cos(Math.toRadians(eulerAngle[1]));
        direction[1] = (float) Math.sin(Math.toRadians(eulerAngle[0]));
        direction[2] = (float) Math.cos(Math.toRadians(eulerAngle[0])) * (float) Math.sin(Math.toRadians(eulerAngle[1]));
        direction = normalize(direction); //转换成单位向量  在运动的时候好计算运动距离  速度
        return genViewMatrix();
    }

    private float[] normalize(float[] in) {
        float[] out = new float[3];
        float length = (float) Math.sqrt(in[0] * in[0] + in[1] * in[1] + in[2] * in[2]);
        out[0] = in[0] / length;
        out[1] = in[1] / length;
        out[2] = in[2] / length;
        return out;
    }

    public void setMovementListener(MovementListener listener) {
        this.movementListener = listener;
    }
}
