package com.aaa.worldmodel;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import com.aaa.worldmodel.surface.WorldRender;
import com.aaa.worldmodel.surface.WorldSurfaceView;
import com.aaa.worldmodel.surface.shape.Cube;
import com.aaa.worldmodel.surface.shape.Triangle;
import com.aaa.worldmodel.surface.texture.Texture2D;

public class MainActivity extends AppCompatActivity {


    private WorldSurfaceView worldSurfaceView;
    private static WorldRender worldRender;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics metrics = new DisplayMetrics();
        getDisplay().getRealMetrics(metrics);
        Log.i("aaaaaaaaaa", " metrics.densityDpi " + metrics.densityDpi + "density : " + metrics.density);
        worldSurfaceView = findViewById(R.id.sv_world);
        worldRender = worldSurfaceView.getRenderer();

//        addTriangle();
//        addColorfulCube();
        addTexture2D();
    }

    public void addTriangle() {
        final float UNIT_SIZE = 0.2f;
        float[] vertex = new float[]{
                0, 0, 0,
                -4 * UNIT_SIZE, 4 * UNIT_SIZE, 0,
                0, 4 * UNIT_SIZE, 0,
                4 * UNIT_SIZE, 4 * UNIT_SIZE, 0,
                4 * UNIT_SIZE, 0, 0,
                4 * UNIT_SIZE, -4 * UNIT_SIZE, 0,
                0, -4 * UNIT_SIZE, 0,
                -4 * UNIT_SIZE, -4 * UNIT_SIZE, 0,
                -4 * UNIT_SIZE, 0, 0,
                -4 * UNIT_SIZE, 4 * UNIT_SIZE, 0,
        };
        float[] color = new float[]{
                1, 1, 1,
                1, 0, 0,
                0, 1, 0,
                0, 0, 1,
                1, 1, 0,
                0, 1, 1,
                1, 0, 1,
                0, 0, 0,
                1, 0, 1,
                1, 0, 0,
        };

        Triangle triangle = Triangle.newBuilder()
                .vertexBuffer(vertex)
                .colorBuffer(color)
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        worldRender.addShape(triangle);
    }

    public void addCube() {
        final float UNIT_SIZE = 0.8f;
        float[] cubeVertex = new float[]{
                -UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//前 左 上
                UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//前 右 上
                UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//前 右 下
                -UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//前 左 下
                -UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//后 左 上
                UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//后 右 上
                UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//后 右 下
                -UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//后 左 下
        };
        int[] index = new int[]{
                0, 1, 2, 0, 2, 3,//正面两个三角形
                4, 5, 6, 4, 6, 7,//背面
                0, 4, 7, 0, 7, 3,//左侧
                1, 5, 6, 1, 6, 2, //右侧
                0, 1, 5, 0, 4, 5,//上
                3, 2, 6, 3, 7, 6
        };
        float[] color = new float[]{
                0, 0, 0,
                0, 0, 0,
                0, 0, 0,
                0, 0, 0,
                1, 1, 0,
                0, 1, 1,
                1, 0, 1,
                0, 0, 0
        };

        Cube cube = new Cube(cubeVertex,index,color);

        worldRender.addShape(cube);
    }

    public void addColorfulCube() {
        final float UNIT_SIZE = 0.8f;
        //立方体每个角拆成3个点 三个点分别不同颜色 后面的数字是指这个点 用在那个面上
        float[] cubeVertex = new float[]{
                //点顺序0
                -UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//0前 左 上 1
                -UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//1前 左 上 5
                -UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//2前 左 上 3

                //点顺序1
                UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//3前 右 上 1
                UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//4前 右 上 4
                UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//5前 右 上 5

                //点顺序2
                UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//6前 右 下 1
                UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//7前 右 下 4
                UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//8前 右 下 6

                //点顺序3
                -UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//9前 左 下 1
                -UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//10前 左 下 3
                -UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//11前 左 下 6

                //点顺序4
                -UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//12后 左 上 2
                -UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//13后 左 上 3
                -UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//14后 左 上 5

                //点顺序5
                UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//15后 右 上 2
                UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//16后 右 上 4
                UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//17后 右 上 5

                //点顺序6
                UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//18后 右 下 2
                UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//19后 右 下 4
                UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//20后 右 下 6

                //点顺序7
                -UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//21后 左 下 2
                -UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//22后 左 下 3
                -UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//23后 左 下 6


        };

        int[] index = new int[]{
                0, 3, 6, 0, 6, 9,//正面 序号1
                12, 15, 18, 12, 18, 21,//背面2
                2, 13, 22, 2, 22, 10,//左侧3
                4, 16, 19, 4,19, 7, //右侧4
                1, 5, 17, 1, 14, 17,//上5
                11, 8, 20, 11, 23, 20 //6
        };

        /*
        1:100
        2:010
        3:001
        4:110
        5:011
        6:101

         */
        float[] color = new float[]{
                1, 0, 0,
                0, 1, 1,
                0, 0, 1,

                1, 0, 0,
                1, 1, 0,
                0, 1, 1,

                1, 0, 0,
                1, 1, 0,
                1, 0, 1,

                1, 0, 0,
                0, 0, 1,
                1, 0, 1,

                0, 1, 0,
                0, 0, 1,
                0, 1, 1,

                0, 1, 0,
                1, 1, 0,
                0, 1, 1,

                0, 1, 0,
                1, 1, 0,
                1, 0, 1,

                0, 1, 0,
                0, 0, 1,
                1, 0, 1
        };

        Cube cube = new Cube(cubeVertex,index,color);

        worldRender.addShape(cube);
    }


    public void addTexture2D(){
        //顶点坐标
        float[] sPos={
                -1.0f,1.0f,    //左上角
                -1.0f,-1.0f,   //左下角
                1.0f,1.0f,     //右上角
                1.0f,-1.0f     //右下角
        };

        //纹理坐标
        float[] sCoord={
                0.0f,0.0f,
                0.0f,1.0f,
                1.0f,0.0f,
                1.0f,1.0f,
        };

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.huskar);
        Texture2D texture2D=new Texture2D(bitmap,sPos,sCoord);
        worldRender.addShape(texture2D);
    }
}
