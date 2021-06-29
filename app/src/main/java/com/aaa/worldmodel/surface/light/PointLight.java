package com.aaa.worldmodel.surface.light;

/**
 * 点光源
 *
 * 衰减系数: 如果后两项为0  则为不衰减
 * 常数项(constant) 通常是1.0，它的作用是保证分母永远不会比1小，因为它可以利用一定的距离增加亮度，这个结果不会影响到我们所寻找的。
 * 一次项(linear) 用于与距离值相乘，这会以线性的方式减少亮度。
 * 二次项(quadratic) 用于与距离的平方相乘，为光源设置一个亮度的二次递减。二次项在距离比较近的时候相比一次项会比一次项更小，但是当距离更远的时候比一次项更大。
 * light= 1 / (constant+ linear * distance + quadratic * distance² )
 */
public class PointLight extends Light {
    float[] position;   //光源位置
    float[] attenuation;     //衰减系数
}
