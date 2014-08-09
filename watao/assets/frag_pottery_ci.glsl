#version 100
precision mediump float;

uniform sampler2D uTexture;
uniform samplerCube uCube;

//light parameter
uniform vec4 uAmbientL;
uniform vec4 uDiffuseL;
uniform vec4 uSpecularL;
uniform vec4 uLightPosition;

//light2
uniform vec4 uAmbientL2;
uniform vec4 uDiffuseL2;
uniform vec4 uSpecularL2;
uniform vec4 uLightPosition2;

//material parameter
uniform vec4 uAmbientM;
uniform vec4 uDiffuseM;
uniform vec4 uSpecularM;
uniform float uShininess;

//look at point;
uniform vec4 uLookAt;

//data from vertex shader
varying vec4 vPosition;
varying vec3 vNormal;    //法向座标
varying vec2 vTexCoord;  //纹理座标
void main(){
    //computer ambient;
    vec4 ambient = uAmbientM * uAmbientL;
    vec4 ambient2 = uAmbientM * uAmbientL2;

    //computer diffuse;
    float dis = length(uLightPosition - vPosition);
    vec4 lightVector = normalize(uLightPosition - vPosition);
    lightVector = normalize(vec4(-1,1,2,0));
    vec4 diffuse = uDiffuseL * uDiffuseM * max(dot(vec3(lightVector), vNormal), 0.0);

    vec4 lightVector2 = normalize(uLightPosition2 - vPosition);
    lightVector2 = vec4(1.2, 0, -1, 1);
    vec4 diffuse2 = uDiffuseL2 * uDiffuseM * max(dot(vec3(lightVector2), vNormal), 0.0);

    //computer specular
    vec4 s = normalize(normalize(uLookAt - vPosition) + lightVector);
    float specularFactor = pow(max(dot(vec3(s), vNormal), 0.0), uShininess);
    vec4 specular = uSpecularL * uSpecularM * specularFactor;

    vec4 s2 = normalize(normalize(uLookAt - vPosition) + lightVector2);
    float specularFactor2 = pow(max(dot(vec3(s2), vNormal), 0.0), uShininess);
    vec4 specular2 = uSpecularL2 * uSpecularM * specularFactor2;

    vec3 texCoordCube = reflect(normalize(uLookAt - vPosition).xyz, vNormal);
    //computer final color
    gl_FragColor = texture2D(uTexture, vTexCoord) 
    	* (diffuse + ambient + diffuse2 + ambient2 )
    	+ specular * 0.4 + specular2 * 1.0
        + textureCube(uCube,texCoordCube) * 0.04;
        
    //vec3 ecPos = vec3(vPosition.x, vPosition.y, vPosition.z);
    //vec3 tnorm = normalize(vNormal);
    //vec3 lightVec = vec3(lightVector.x, lightVector.y, lightVector.z);
    //vec3 lightVec2 = vec3(lightVector2.x, lightVector2.z, lightVector2.z);
    
    //float NdotL = (dot(lightVec, tnorm) + 1.0) * 0.5;
    //float NdotL2 = (dot(lightVec2, tnorm) + 1.0) * 0.5;
    //vec3 ReflectVec = normalize(reflect(-lightVec, tnorm));
    //vec3 ReflectVec2 = normalize(reflect(-lightVec2, tnorm));
   // vec3 ViewVec = normalize(-ecPos);
 
    //vec3 SurfaceColor = vec3(0.9, 0.9, 1.0);
    //vec3 WarmColor = vec3(0.2, 0.2, 0.2);
    //vec3 CoolColor = vec3(0.0, 0.0, 0.0);
   // float DiffuseWarm = 0.45;
    //float DiffuseCool = 0.45;
    
    //vec3 kcool = min(CoolColor + DiffuseCool * SurfaceColor, 1.0);
    //vec3 kwarm = min(WarmColor + DiffuseWarm * SurfaceColor, 1.0);
    
    //vec3 kfinal = mix(kcool, kwarm, NdotL);
    //vec3 kfinal2 = mix(kcool, kwarm, NdotL2);
    
    //vec3 nreflect = normalize(ReflectVec);
   // vec3 nreflect2 = normalize(ReflectVec2);
    //vec3 nview = normalize(ViewVec);
    
    //float spec = max(dot(nreflect, nview), 0.0);
    //float spec2 = max(dot(nreflect2, nview), 0.0);
    //spec = pow(spec, 5.0);
    //spec2 = pow(spec2, 5.0);
    
    //vec4 texColorVec4 = texture2D(uTexture, vTexCoord);
    //vec3 texColorVec3 = vec3(texColorVec4.x, texColorVec4.y, texColorVec4.z);
    
    //vec4 envColorVec4 = textureCube(uCube,texCoordCube);
    //vec3 envColorVec3 = vec3(envColorVec4.x, envColorVec4.y, envColorVec4.z);
    
    //gl_FragColor = vec4(min(texColorVec3 * kfinal * 0.5 + spec * 0.1, 1.0), 1.0);
    //gl_FragColor = vec4(min(texColorVec3 * kfinal2 * 1.0 + spec2 * 0.0, 1.0), 1.0);
    //gl_FragColor = vec4(min( texColorVec3 * kfinal * 0.7 + texColorVec3 * kfinal2 * 0.2 + spec * 0.1 + spec2 * 0.0, 1.0), 1.0);
    //gl_FragColor = vec4(min( texColorVec3 * kfinal * 0.7 + texColorVec3 * kfinal2 * 0.2 + spec * 0.1 + spec2 * 0.0, 1.0), 1.0);
    //gl_FragColor = vec4(min( texColorVec3 * kfinal * 0.9 + spec * 0.1 + envColorVec3 * 0.05, 1.0), 1.0);
    //gl_FragColor = envColorVec4;
}

