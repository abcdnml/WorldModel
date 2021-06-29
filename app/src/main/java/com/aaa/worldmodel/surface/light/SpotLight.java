package com.aaa.worldmodel.surface.light;

/**
 * 聚光
 *
 * 手电筒(Flashlight)是一个坐落在观察者位置的聚光，通常瞄准玩家透视图的前面。基本上说，一个手电筒是一个普通的聚光，但是根据玩家的位置和方向持续的更新它的位置和方向。
 * 另外 为了光圈边缘显示时 平滑变暗, 可以多设置一个外切圆锥,  在内圆锥中亮度是1  外圆锥 亮度从1减弱到0
 */
public class SpotLight extends Light {
    float[] position;       //光源位置
    float[] direction;      //光照射方向
    float inCutOff;         //光扩散角度1
    float outCutOff;        //光扩散角度2    用做边缘亮度衰减
}
