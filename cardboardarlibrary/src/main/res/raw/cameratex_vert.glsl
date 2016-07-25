attribute vec3 a_Position;
attribute vec2 a_TexCoordinate;

varying vec2 v_TexCoordinate;

void main() {
    // Pass through the texture coordinate.
   v_TexCoordinate = a_TexCoordinate;

   gl_Position = vec4(a_Position, 1.0);
}
