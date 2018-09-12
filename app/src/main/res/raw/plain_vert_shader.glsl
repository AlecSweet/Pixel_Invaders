
attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 a_TexCoordinate; 
 
uniform float x_Scale;
uniform float y_Scale;
uniform vec2 displacement;

varying vec2 v_TexCoordinate;
varying vec4 v_Color;

void main()
{
	v_Color = a_Color;
	v_TexCoordinate = a_TexCoordinate;
	
	gl_Position = a_Position;
			
	gl_Position.x = (a_Position.x + displacement.x) * x_Scale;
	gl_Position.y = (a_Position.y + displacement.y) * y_Scale;
}