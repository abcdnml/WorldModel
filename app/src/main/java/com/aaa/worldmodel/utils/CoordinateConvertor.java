package com.aaa.worldmodel.utils;

import android.graphics.PointF;
import android.opengl.GLES30;
import android.opengl.Matrix;

import java.util.Arrays;

/**
 * 3d坐标和屏幕坐标互相转换
 *
 * 转换有问题 无法使用
 */
public class CoordinateConvertor {
    private static final float[] _tempGluUnProjectData = new float[40];
    private static final int _temp_m = 0;
    private static final int _temp_A = 16;
    private static final int _temp_in = 32;
    private static final int _temp_out = 36;
    /**
     * 将物体坐标转换成世界坐标
     *
     * @param matrixLeft
     * @param vectorRight
     * @return
     */
    public static PointF4 getTranslatePoint(float[] matrixLeft, PointF3 vectorRight) {
        PointF4 p4 = GLKMatrix4MultiplyVector4(matrixLeft, new PointF4(vectorRight.x, vectorRight.y, vectorRight.z, 1.0f));
//        return PointF3MultiplyScalar(new PointF3(v4.x, v4.y, v4.z), 1.0f / v4.w);
        return new PointF4(p4.x / p4.w, p4.y / p4.w, p4.z / p4.w, 1.0f / p4.w);
    }

    public static PointF4 GLKMatrix4MultiplyVector4(float[] matrixLeft, PointF4 vector) {
        PointF4 v4 = new PointF4();
        v4.x = matrixLeft[0] * vector.x + matrixLeft[4] * vector.y + matrixLeft[8] * vector.z + matrixLeft[12] * vector.w;
        v4.y = matrixLeft[1] * vector.x + matrixLeft[5] * vector.y + matrixLeft[9] * vector.z + matrixLeft[13] * vector.w;
        v4.z = matrixLeft[2] * vector.x + matrixLeft[6] * vector.y + matrixLeft[10] * vector.z + matrixLeft[14] * vector.w;
        v4.w = matrixLeft[3] * vector.x + matrixLeft[7] * vector.y + matrixLeft[11] * vector.z + matrixLeft[15] * vector.w;
        return v4;
    }

    public static int gluUnProject(float winx, float winy, float winz,
                                   float[] model,
                                   float[] proj,
                                   int[] viewport,
                                   float[] xyz) {
        /* Normalize between -1 and 1 */
        _tempGluUnProjectData[_temp_in] = (winx - viewport[0]) * 2f / viewport[2] - 1.0f;
        _tempGluUnProjectData[_temp_in + 1] = (winy - viewport[1]) * 2f / viewport[3] - 1.0f;
        _tempGluUnProjectData[_temp_in + 2] = 2f * winz - 1.0f;
        _tempGluUnProjectData[_temp_in + 3] = 1.0f;
        /* Get the inverse */
        Matrix.multiplyMM(_tempGluUnProjectData, _temp_A, proj, 0, model, 0);
        Matrix.invertM(_tempGluUnProjectData, _temp_m, _tempGluUnProjectData, _temp_A);
        Matrix.multiplyMV(_tempGluUnProjectData, _temp_out,
                _tempGluUnProjectData, _temp_m,
                _tempGluUnProjectData, _temp_in);
        if (_tempGluUnProjectData[_temp_out + 3] == 0.0) {
            return GLES30.GL_FALSE;
        }
        xyz[0] = _tempGluUnProjectData[_temp_out] / _tempGluUnProjectData[_temp_out + 3];
        xyz[1] = _tempGluUnProjectData[_temp_out + 1] / _tempGluUnProjectData[_temp_out + 3];
        xyz[2] = _tempGluUnProjectData[_temp_out + 2] / _tempGluUnProjectData[_temp_out + 3];
        return GLES30.GL_TRUE;
    }

    public static float[] unProject(int x, int y,
                                    int viewWidth, int viewHeight,
                                    float[] proj,
                                    float[] model)
    {
        float z = 0;
        float[] xyz=new float[3];
        int[] viewport=new int[4];
        viewport[0]=0;
        viewport[1]=0;
        viewport[2]=viewWidth;
        viewport[3]=viewHeight;
        gluUnProject(x, viewHeight -y, z, model, proj, viewport, xyz);
        return xyz;
    }


    public static PointF getScreenPointBy3d(float x, float y, float z, float w, float h, float[] mProjMatrix, float[] mVMatrix, float[] modelMatrix) {
        float[] temp1 = new float[16];
        float[] temp2 = new float[16];
        float[] result = new float[4];
        LogUtils.i("press: " + new PointF3(x, y, z).toString());
        float[] input=new float[]{x,y,z,1};


        Matrix.multiplyMM(temp1, 0, mProjMatrix, 0, mVMatrix, 0);
        Matrix.multiplyMM(temp2, 0, temp1, 0, modelMatrix, 0);
        Matrix.multiplyMV(result, 0,  temp2,  0,input,0);
        LogUtils.i(" matrix result  "+ Arrays.toString(result) );

        PointF4 pointF4 = getTranslatePoint(temp2, new PointF3(x, y, z));
        LogUtils.i("result p4 " + pointF4);
        PointF p = new PointF(pointF4.x * w / 2 +w / 2, h / 2 * pointF4.y + h / 2);
        LogUtils.i("result screen" + p);
        return p;
    }

    public static void get3DPointByScreen(float x, float y,float w,float h,float[] mProjMatrix,float[] mVMatrix,float[] modelMatrix) {
        float[] temp1 = new float[16];
        float[] temp2 = new float[16];
        float[] temp3 = new float[16];

        Matrix.multiplyMM(temp1, 0, mProjMatrix, 0, mVMatrix, 0);
        Matrix.multiplyMM(temp2, 0, temp1, 0, modelMatrix, 0);
        Matrix.invertM(temp3, 0, temp2, 0);
        LogUtils.i("result:x y : " + x + ", " + y);

        PointF4 pointF4 = getTranslatePoint(temp2, new PointF3(x / (w / 2) - 1, 1, y / (h / 2) - 1));
        LogUtils.i("result: " + pointF4.toString());

    }

    public static void get3DPointByScreen1(float x, float y,float w,float h,float[] mProjMatrix,float[] mVMatrix,float[] modelMatrix) {
        LogUtils.i("screen:x y : " + x + ", " + y);
        float[] xyz = unProject((int) x, (int) y,(int) w, (int)h, mProjMatrix, modelMatrix);
        LogUtils.i("result:xyz: " + xyz[0] + "," + xyz[1] + ", " + xyz[2]);

    }

}
