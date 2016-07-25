uniform mat4 u_MVP;

attribute vec3 a_Position;
attribute vec4 a_TexCoordinate;

varying vec2 v_TexCoordinate;

void main() {
    // Pass through the texture coordinate.
   v_TexCoordinate = a_TexCoordinate.xy;

   gl_Position = u_MVP * vec4(a_Position, 1.0);
}
