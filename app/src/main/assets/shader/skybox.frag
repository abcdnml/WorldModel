#version 300 es
precision mediump float;

in vec3 TexCoords;
out vec4 color;

uniform samplerCube skybox;

void main()
{
    color = texture(skybox, TexCoords);
    color=color+0.2;
//    color = vec4(0.6);
}