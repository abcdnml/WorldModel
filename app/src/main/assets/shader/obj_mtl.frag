# version 300 es
precision mediump float;

in vec2 TexCoords;
in vec3 Normal;
in vec3 FragPos;



out vec4 fragColor;

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    sampler2D map_ka;
    sampler2D map_kd;
    sampler2D map_ks;
    float shininess;
};

struct PointLight {
//点光源参数
    vec3 position;
    int attenuation;//是否衰减 0=无衰减 1=有衰减
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
//点光源衰减参数
    float constant;
    float linear;
    float quadratic;
};

struct DirLight {
//平行光参数
    vec3 direction;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

uniform Material material;
uniform PointLight pointLight;
uniform DirLight dirLight;
uniform int lightType;//1-点光源 2-平行光
uniform vec3 viewPos;//Camera观察者坐标

vec3 k_pointLight()
{
    //点光源
    // ambient 环境光照
    vec3 ambient = pointLight.ambient * material.ambient;

    // diffuse 漫反射光照
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(pointLight.position - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = pointLight.diffuse * (diff * material.diffuse);

    // specular 镜面光照
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = pointLight.specular * (spec * material.specular);

    //点光源衰减
    if (pointLight.attenuation == 1) {
        float distance = length(pointLight.position - FragPos);
        float kattenuation = 1.0 / (pointLight.constant + pointLight.linear * distance + pointLight.quadratic * (distance * distance));
        ambient = ambient * kattenuation;
        diffuse = diffuse * kattenuation;
        specular = specular * kattenuation;
    }

    vec3 result = ambient + diffuse + specular;
    return result;
}

vec3 k_directionalLight()
{
    //平行光 太阳光
    // ambient 环境光照
    vec3 ambient = dirLight.ambient * material.ambient ;

    // diffuse 漫反射光照
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(-dirLight.direction);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = dirLight.diffuse * (diff * material.diffuse);

    // specular 镜面光照
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = dirLight.specular * (spec * material.specular);

    vec3 result = ambient + diffuse + specular;
    return result;
}

void main()
{
    vec3 result;
    if (lightType == 1) {
        //点光源
        result = k_pointLight();
    } else if (lightType == 2) {
        //平行光
        result = k_directionalLight();
    }

    fragColor = vec4(result, 1.0);
}

