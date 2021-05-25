package com.aaa.worldmodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.aaa.worldmodel.surface.WorldRender;
import com.aaa.worldmodel.surface.WorldSurfaceView;
import com.aaa.worldmodel.surface.obj.MtlInfo;
import com.aaa.worldmodel.surface.obj.Obj3D;
import com.aaa.worldmodel.surface.obj.Obj3DShape;
import com.aaa.worldmodel.surface.obj.Obj3DShape1;
import com.aaa.worldmodel.surface.obj.ObjReader;
import com.aaa.worldmodel.surface.obj.ObjShape;
import com.aaa.worldmodel.surface.shape.Cube;
import com.aaa.worldmodel.surface.shape.Triangle;
import com.aaa.worldmodel.surface.texture.ImageHandle;
import com.aaa.worldmodel.surface.texture.Texture2D;
import com.aaa.worldmodel.utils.LogUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import static java.lang.Math.PI;

public class MainActivity extends AppCompatActivity {


    private static WorldRender worldRender;
    private WorldSurfaceView worldSurfaceView;
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
//        addCircle();
//        addCone();
//        addCube();
//        addColorfulCube();
//        addColumn();
//        addGlobe();
//        addTexture2D();
//        addTexture2DEffect();
//        add3DObj();
//        addMulti3DObj();
//        test3D();
        map();
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
                .context(this)
                .vertexBuffer(vertex)
                .colorBuffer(color)
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        worldRender.addShape(triangle);
    }

    public void addCircle() {
        int triangleCount = 100;
        final float UNIT_SIZE = 0.4f;

        FloatBuffer vertexBuffer = FloatBuffer.allocate((triangleCount + 2) * 3);
        FloatBuffer colorBuffer = FloatBuffer.allocate((triangleCount + 2) * 3);

        //添加圆心 使用GL_TRIANGLE_FAN模式绘制
        vertexBuffer.put(0f);
        vertexBuffer.put(0f);
        vertexBuffer.put(0f);
        colorBuffer.put(0f);
        colorBuffer.put(0f);
        colorBuffer.put(1f);

        //添加圆周的点
        for (int i = 0; i <= triangleCount; i++) {
            vertexBuffer.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBuffer.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBuffer.put(0f);

            colorBuffer.put(0f);
            colorBuffer.put(0f);
            colorBuffer.put(1f);
        }
        Triangle circle = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBuffer.array())
                .colorBuffer(colorBuffer.array())
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        worldRender.addShape(circle);

    }

    public void addCone() {
        int triangleCount = 100;
        final float UNIT_SIZE = 0.4f;
        FloatBuffer vertexBuffer = FloatBuffer.allocate((triangleCount + 2) * 3);
        FloatBuffer colorBuffer = FloatBuffer.allocate((triangleCount + 2) * 4);
        vertexBuffer.put(0f);
        vertexBuffer.put(0f);
        vertexBuffer.put(0f);
        colorBuffer.put(0f);
        colorBuffer.put(0f);
        colorBuffer.put(1f);

        FloatBuffer vertexBufferUp = FloatBuffer.allocate((triangleCount + 2) * 3);
        FloatBuffer colorBufferUp = FloatBuffer.allocate((triangleCount + 2) * 4);
        vertexBufferUp.put(0f);
        vertexBufferUp.put(0f);
        vertexBufferUp.put(1f);
        colorBufferUp.put(0f);
        colorBufferUp.put(0f);
        colorBufferUp.put(1f);

        for (int i = 0; i <= triangleCount; i++) {
            vertexBuffer.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBuffer.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBuffer.put(0f);

            colorBuffer.put(0f);
            colorBuffer.put(0f);
            colorBuffer.put(1f);

            vertexBufferUp.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBufferUp.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBufferUp.put(0f);

            colorBufferUp.put(0f);
            colorBufferUp.put(1f);
            colorBufferUp.put(0f);
        }


        Triangle circle = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBuffer.array())
                .colorBuffer(colorBuffer.array())
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();
        worldRender.addShape(circle);

        Triangle circle1 = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBufferUp.array())
                .colorBuffer(colorBufferUp.array())
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        worldRender.addShape(circle1);
    }


    public void addColumn() {
        int triangleCount = 100;
        final float UNIT_SIZE = 0.4f;
        FloatBuffer vertexBufferBottom = FloatBuffer.allocate((triangleCount + 2) * 3);
        vertexBufferBottom.put(0f);
        vertexBufferBottom.put(0f);
        vertexBufferBottom.put(0f);

        FloatBuffer colorBufferBottom = FloatBuffer.allocate((triangleCount + 2) * 3);
        colorBufferBottom.put(0f);
        colorBufferBottom.put(0f);
        colorBufferBottom.put(1f);

        FloatBuffer vertexBufferTop = FloatBuffer.allocate((triangleCount + 2) * 3);
        vertexBufferTop.put(0f);
        vertexBufferTop.put(0f);
        vertexBufferTop.put(1f);

        FloatBuffer colorBufferTop = FloatBuffer.allocate((triangleCount + 2) * 3);
        colorBufferTop.put(0.9f);
        colorBufferTop.put(0.9f);
        colorBufferTop.put(0.9f);

        FloatBuffer vertexBufferBorder = FloatBuffer.allocate((triangleCount + 1) * 2 * 3);
        FloatBuffer colorBufferBorder = FloatBuffer.allocate((triangleCount + 1) * 2 * 3);

        for (int i = 0; i <= triangleCount; i++) {
            //画底部的圆
            vertexBufferBottom.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBufferBottom.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBufferBottom.put(0f);

            colorBufferBottom.put(0f);
            colorBufferBottom.put(0f);
            colorBufferBottom.put(1f);

            //画顶部的圆
            vertexBufferTop.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBufferTop.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBufferTop.put(1f);

            colorBufferTop.put(0.8f);
            colorBufferTop.put(0.8f);
            colorBufferTop.put(0.8f);


            //画圆筒
            vertexBufferBorder.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBufferBorder.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBufferBorder.put(0);
            colorBufferBorder.put(0.8f);
            colorBufferBorder.put(0.8f);
            colorBufferBorder.put(0.8f);

            vertexBufferBorder.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBufferBorder.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBufferBorder.put(1);
            colorBufferBorder.put(0.9f);
            colorBufferBorder.put(0.9f);
            colorBufferBorder.put(0.9f);
        }
        Triangle circleDown = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBufferBottom.array())
                .colorBuffer(colorBufferBottom.array())
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        Triangle circleUp = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBufferTop.array())
                .colorBuffer(colorBufferTop.array())
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        Triangle border = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBufferBorder.array())
                .colorBuffer(colorBufferBorder.array())
                .drawType(GLES30.GL_TRIANGLE_STRIP)
                .build();

        worldRender.addShape(circleUp);
        worldRender.addShape(circleDown);
        worldRender.addShape(border);

    }

    public void addGlobe() {

        int triangleCount = 20;
        final float UNIT_SIZE = 0.4f;
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect((triangleCount + 1) * (triangleCount + 1) * 2 * 3 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        FloatBuffer colorBuffer = ByteBuffer.allocateDirect((triangleCount + 1) * (triangleCount + 1) * 2 * 3 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        for (int i = -90; i <= 90; i += 180 / triangleCount) {
            float z1 = UNIT_SIZE * (float) Math.sin(PI / 180 * i);
            float r1 = UNIT_SIZE * (float) Math.cos(PI / 180 * i);

            float z2 = UNIT_SIZE * (float) Math.sin(PI / 180 * (i + 180f / triangleCount));
            float r2 = UNIT_SIZE * (float) Math.cos(PI / 180 * (i + 180f / triangleCount));

            for (int j = 0; j <= 360; j += 360 / triangleCount) {
                float x1 = r1 * (float) Math.cos(PI / 180 * j);
                float y1 = r1 * (float) Math.sin(PI / 180 * j);

                float x2 = r2 * (float) Math.cos(PI / 180 * j);
                float y2 = r2 * (float) Math.sin(PI / 180 * j);

                Log.i("x1 y1 z1 ", " (" + x1 + "," + y1 + "," + z1 + ")");
                vertexBuffer.put(x1);
                vertexBuffer.put(y1);
                vertexBuffer.put(z1);
                colorBuffer.put((i + 90) / 180f);
                colorBuffer.put((i + 90) / 180f);
                colorBuffer.put((i + 90) / 180f);

                Log.i("x2 y2 z2 ", " (" + x2 + "," + y2 + "," + z2 + ")");
                vertexBuffer.put(x2);
                vertexBuffer.put(y2);
                vertexBuffer.put(z2);
                colorBuffer.put((i + 90) / 180f);
                colorBuffer.put((i + 90) / 180f);
                colorBuffer.put((i + 90) / 180f);
            }
        }
        vertexBuffer.flip();
        colorBuffer.flip();
        Triangle border = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBuffer)
                .colorBuffer(colorBuffer)
                .drawType(GLES30.GL_TRIANGLE_STRIP)
                .build();

        worldRender.addShape(border);


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

        Cube cube = new Cube(this, cubeVertex, index, color);

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
                4, 16, 19, 4, 19, 7, //右侧4
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

        Cube cube = new Cube(this, cubeVertex, index, color);

        worldRender.addShape(cube);
    }


    public void addTexture2D() {
        //顶点坐标
        float[] sPos = {
                -1.0f, 1.0f,    //左上角
                -1.0f, -1.0f,   //左下角
                1.0f, 1.0f,     //右上角
                1.0f, -1.0f     //右下角
        };

        //纹理坐标
        float[] sCoord = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
        };

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.huskar);
        Texture2D texture2D = new Texture2D(this, bitmap, sPos, sCoord);
        worldRender.addShape(texture2D);
    }

    public void addTexture2DEffect() {
        //顶点坐标
        float[] sPos = {
                -1.0f, 1.0f,    //左上角
                -1.0f, -1.0f,   //左下角
                1.0f, 1.0f,     //右上角
                1.0f, -1.0f     //右下角
        };

        //纹理坐标
        float[] sCoord = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
        };
        FloatBuffer vertexCoordinate = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(sPos);
        vertexCoordinate.flip();
        FloatBuffer textureCoordinate = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(sCoord);
        textureCoordinate.flip();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.huskar);
        ImageHandle imageHandle = new ImageHandle(this, bitmap, vertexCoordinate, textureCoordinate, 4, new float[]{0f, 1f, 0f}, true);
        worldRender.addShape(imageHandle);
    }

    public void add3DObj() {
        Obj3DShape obj3DShape1 = new Obj3DShape(this);
        obj3DShape1.temp = true;
        Obj3DShape obj3DShape2 = new Obj3DShape(this);
        worldRender.addShape(obj3DShape1);
        worldRender.addShape(obj3DShape2);
    }

    public void addMulti3DObj() {
/*        List<Obj3D> multiObj= ObjReader.readMultiObj(this,"assets/obj/pikachu.obj");
        for(Obj3D obj3D: multiObj){
            Obj3DShape1 obj3DShape1=new Obj3DShape1(this,obj3D,0.008f);
            worldRender.addShape(obj3DShape1);
        }
        List<Obj3D> multiObj1= ObjReader.readMultiObj(this,"assets/obj/patrick.obj");
        for(Obj3D obj3D: multiObj1){
            Obj3DShape1 obj3DShape1=new Obj3DShape1(this,obj3D,1.0f);
            worldRender.addShape(obj3DShape1);
        }*/

//        List<Obj3D> multiObj2= ObjReader.readMultiObj(this,"assets/obj/床头柜.obj");
//        LogUtils.i("obj size: "+multiObj2.size());
//        for(Obj3D obj3D: multiObj2){
//            Obj3DShape1 obj3DShape1=new Obj3DShape1(this,obj3D,0.008f);
//            worldRender.addShape(obj3DShape1);
//        }

//        List<Obj3D> multiObj2= ObjReader.readMultiObj(this,"assets/obj/Bigmax_White_OBJ.obj");
//        LogUtils.i("obj size: "+multiObj2.size());
//        for(Obj3D obj3D: multiObj2){
//            Obj3DShape1 obj3DShape1=new Obj3DShape1(this,obj3D,0.008f);
//            worldRender.addShape(obj3DShape1);
//        }

        List<Obj3D> multiObj2 = ObjReader.readMultiObj(this, "assets/obj/test.obj");
        LogUtils.i("obj size: " + multiObj2.size());
        for (Obj3D obj3D : multiObj2) {
            Obj3DShape1 obj3DShape1 = new Obj3DShape1(this, obj3D, 1);
            worldRender.addShape(obj3DShape1);
        }
    }

    public void test3D() {
        List<Obj3D> multiObj2 = ObjReader.readMultiObj(this, "assets/obj/bigwhite/Bigmax_White_OBJ.obj");
        for (Obj3D obj3D : multiObj2) {
            ObjShape objShape = new ObjShape(this, obj3D, 0.01f);
            worldRender.addShape(objShape);
        }
    }

    public void map() {
        Gson gson = new Gson();
        LDMapBean ldMapBean = gson.fromJson(readAssetString("test/map.json"), LDMapBean.class);
        int[] mapdata = new int[ldMapBean.baseMapData.length()];
        for (int i = 0; i < ldMapBean.baseMapData.length(); i++) {
            mapdata[i] = ldMapBean.baseMapData.charAt(i) - '0';
        }
        Obj3D obj3D = arrayToObj(ldMapBean.width, ldMapBean.height, mapdata, ldMapBean.resolution);
        ObjShape objShape = new ObjShape(this, obj3D, 1f);
        worldRender.addShape(objShape);

    }

    public String readAssetString(String path) {
        String parent = path.substring(0, path.lastIndexOf("/") + 1);
        String tmp = null;
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(path)));
            while ((tmp = br.readLine()) != null) {
                buffer.append(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tmp = buffer.toString();
        LogUtils.ls(tmp);
        return tmp;
    }

    //数据转换成obj格式
    public Obj3D arrayToObj(int width, int height, int[] data, float resolution) {

        MtlInfo mtlInfo = MtlInfo.newBuilder()
                .Ka(new float[]{0.8f, 0.8f, 0.8f})
                .Kd(new float[]{0.8f, 0.8f,0f})
                .Ks(new float[]{0.8f, 0.8f, 0.8f})
                .Ke(new float[]{1f, 1f, 1f})
                .Ns(32)
                .illum(2)
                .build();


        FloatBuffer vertex = ByteBuffer.allocateDirect(width * height * 6 * 6 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer vertexNormal = ByteBuffer.allocateDirect(width * height * 6 * 6 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer vertexTexture = ByteBuffer.allocateDirect(width * height * 6 * 6 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();



        int minX=width;
        int minZ=height;
        int maxX=0;
        int maxZ=0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int type = data[i * width + j];
                if (type == 0) {
                    //0 墙内
//                    genCube(vertex, vertexTexture, vertexNormal, j, i, width, height, resolution);
                } else if (type == 1) {
                    //1 墙
                    genWall(vertex, vertexTexture, vertexNormal, j, i, width, height, resolution);
                } else if(type==2){
                    //2 墙外
                }else{

                }
            }
        }
        //直接画宽高大小的地板
        genFloor(vertex, vertexTexture, vertexNormal,width,height,resolution);

        vertex.flip();
        vertexNormal.flip();
        vertexTexture.flip();
        LogUtils.i(vertex.toString());
        Obj3D obj3D = Obj3D.newBuilder()
                .mtl(mtlInfo)
                .vert(vertex)
                .vertNorl(vertexNormal)
                .vertTexture(vertexTexture)
                .vertCount(vertex.capacity() / 3)
                .build();
        return obj3D;
    }

    public void genCube(FloatBuffer v, FloatBuffer vt, FloatBuffer vn, int x, int z, int width, int height, float resolution) {

        float offsetX = x * resolution - width * resolution / 2;
        float offsetY = -resolution / 2;
        float offsetZ = z * resolution - height * resolution / 2;
        float rectX = resolution;
        float rectY = resolution;
        float rectZ = resolution;
        addCuboidVertex(v, vt, vn, rectX, rectY, rectZ, offsetX, offsetY, offsetZ);
    }

    public void genWall(FloatBuffer v, FloatBuffer vt, FloatBuffer vn, int x, int z, int width, int height, float resolution) {

        float offsetX = x * resolution - width * resolution / 2;
        float offsetY = resolution / 2;
        float offsetZ = z * resolution - height * resolution / 2;
        float rectX = resolution;
        float rectY = 10 * resolution;
        float rectZ = resolution;
        addCuboidVertex(v, vt, vn, rectX, rectY, rectZ, offsetX, offsetY, offsetZ);
    }

    public void genFloor(FloatBuffer v, FloatBuffer vt, FloatBuffer vn, int width, int height, float resolution) {
        float offsetX = -width * resolution / 2;
        float offsetY = -resolution / 2;
        float offsetZ = -height * resolution / 2;
        float rectX = width * resolution;
        float rectY = resolution;
        float rectZ = height * resolution;
        addCuboidVertex(v, vt, vn, rectX, rectY, rectZ, offsetX, offsetY, offsetZ);
    }

    public void addCuboidVertex(FloatBuffer v, FloatBuffer vt, FloatBuffer vn, float rectX, float rectY, float rectZ, float offsetX, float offsetY, float offsetZ) {
        float[] vertex = new float[]{
                -rectX + offsetX, rectY + offsetY, rectZ + offsetZ,//前 左 上
                rectX + offsetX, rectY + offsetY, rectZ + offsetZ,//前 右 上
                rectX + offsetX, -rectY + offsetY, rectZ + offsetZ,//前 右 下
                -rectX + offsetX, -rectY + offsetY, rectZ + offsetZ,//前 左 下
                -rectX + offsetX, rectY + offsetY, -rectZ + offsetZ,//后 左 上
                rectX + offsetX, rectY + offsetY, -rectZ + offsetZ,//后 右 上
                rectX + offsetX, -rectY + offsetY, -rectZ + offsetZ,//后 右 下
                -rectX + offsetX, -rectY + offsetY, -rectZ + offsetZ,//后 左 下
        };

        int[] index = new int[]{
                0, 1, 2, 0, 2, 3,//正面两个三角形
                4, 5, 6, 4, 6, 7,//背面
                0, 4, 7, 0, 7, 3,//左侧
                1, 5, 6, 1, 6, 2, //右侧
                0, 1, 5, 0, 5, 4,//上
                3, 2, 6, 3, 6, 7
        };
        float[] normal = new float[]{
                0, 1, 0,//上
                0, -1, 0,//下
                -1, 0, 0,//左
                1, 0, 0,//右
                0, 0, 1,//前
                0, 0, -1//后
        };
        int[] normalIndex = new int[]{
                4, 4, 4, 4, 4, 4,//正面两个三角形
                5, 5, 5, 5, 5, 5,//背面
                2, 2, 2, 2, 2, 2,//左侧
                3, 3, 3, 3, 3, 3, //右侧
                0, 0, 0, 0, 0, 0,//上
                1, 1, 1, 1, 1, 1//下
        };

        float[] texture = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
        };

        int[] textureIndex = new int[]{
                0, 1, 2, 0, 2, 3,//正面两个三角形
                1, 0, 3, 1, 3, 2,//背面
                1, 0, 3, 1, 3, 2,//左侧
                0, 1, 2, 0, 2, 3, //右侧
                3, 2, 1, 3, 1, 0,//上
                0, 1, 2, 0, 2, 3//下
        };

        for (int i = 0; i < index.length; i++) {
            v.put(vertex[index[i] * 3]);
            v.put(vertex[index[i] * 3 + 1]);
            v.put(vertex[index[i] * 3 + 2]);

            vn.put(normal[normalIndex[i] * 3]);
            vn.put(normal[normalIndex[i] * 3 + 1]);
            vn.put(normal[normalIndex[i] * 3 + 2]);

            vt.put(texture[textureIndex[i] * 2]);
            vt.put(texture[textureIndex[i] * 2 + 1]);
        }
    }

}
