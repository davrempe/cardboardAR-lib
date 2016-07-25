// important to include in order to use rendered Android View to gl texture
#extension GL_OES_EGL_image_external : require

//make sure to use samplerExternalOES instead of sampler2D
uniform samplerExternalOES u_Texture;    // The input texture.

precision mediump float;

varying vec2 v_TexCoordinate;

void main() {
    gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
}
