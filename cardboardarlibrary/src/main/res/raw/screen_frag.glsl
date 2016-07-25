uniform sampler2D u_cameraTexture;    // The input camera feed texture.
uniform sampler2D u_objectsTexture;    // The input objects scene texture.

precision mediump float;

varying vec2 v_TexCoordinate;

void main() {
    vec4 objectColor = texture2D(u_objectsTexture, v_TexCoordinate);
    vec4 cameraColor = texture2D(u_cameraTexture, v_TexCoordinate);

    if (objectColor.a == 1.0) {
        gl_FragColor = objectColor;
    } else if (objectColor.a == 0.0){
        gl_FragColor = cameraColor;
    } else {
         gl_FragColor = cameraColor + objectColor;
    }

//    gl_FragColor = objectColor;

//    gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}
