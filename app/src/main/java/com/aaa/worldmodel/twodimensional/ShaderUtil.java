package com.aaa.worldmodel.twodimensional;

/**
 * Description
 * Created by aaa on 2017/11/16.
 */

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.aaa.worldmodel.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

//加载顶点Shader与片元Shader的工具类
public class ShaderUtil
{
    //从assert中加载shader内容的方法
    public static String loadFromAssetsFile(String fname,Resources r)
    {
        String result=null;
        try
        {
            InputStream in=r.getAssets().open(fname);
            int ch=0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((ch=in.read())!=-1)
            {
                baos.write(ch);
            }
            byte[] buff=baos.toByteArray();
            baos.close();
            in.close();
            result=new String(buff,"UTF-8");
            result=result.replaceAll("\\r\\n","\n");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }


    public static int createProgram(String vertexShaderCode, String fragmentShaderCode) {
        int vertexShaderId = compileShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderId = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
        if (vertexShaderId == 0 || fragmentShaderId == 0) {
            LogUtils.e( " shader id is 0  vertex :" + vertexShaderId + " color: " + fragmentShaderId);
            return 0;
        }

        int programId = linkProgram(vertexShaderId, fragmentShaderId);
        if (programId == 0) {
            LogUtils.e( " program id is 0");
            return 0;
        }
        return programId;
    }
    /**
     * 链接小程序
     *
     * @param vertexShaderId   顶点着色器
     * @param fragmentShaderId 片段着色器
     * @return
     */
    private static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = GLES30.glCreateProgram();
        LogUtils.e( "linkProgram programId: " + programId);
        if (programId != 0) {
            //将顶点着色器加入到程序
            GLES30.glAttachShader(programId, vertexShaderId);
            //将片元着色器加入到程序中
            GLES30.glAttachShader(programId, fragmentShaderId);
            //链接着色器程序
            GLES30.glLinkProgram(programId);
            final int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                String logInfo = GLES30.glGetProgramInfoLog(programId);
                LogUtils.e( "linkProgram err: " + logInfo);
                GLES30.glDeleteProgram(programId);
                return 0;
            }
            return programId;
        } else {
            //创建失败
            return 0;
        }
    }

    /**
     * 编译
     *
     * @param type       顶点着色器:GLES30.GL_VERTEX_SHADER
     *                   片段着色器:GLES30.GL_FRAGMENT_SHADER
     * @param shaderCode
     * @return
     */
    protected static int compileShader(int type, String shaderCode) {
        //创建一个着色器
        final int shaderId = GLES30.glCreateShader(type);
        LogUtils.e( "glCreateShader : " + shaderId);
        if (shaderId != 0) {
            //加载到着色器
            GLES30.glShaderSource(shaderId, shaderCode);
            //编译着色器
            GLES30.glCompileShader(shaderId);
            //检测状态
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                String logInfo = GLES30.glGetShaderInfoLog(shaderId);
                System.err.println(logInfo);
                LogUtils.e( "compileShader : " + type + " " + logInfo);
                //创建失败
                GLES30.glDeleteShader(shaderId);
                return 0;
            }
            return shaderId;
        } else {
            //创建失败
            return 0;
        }
    }

}