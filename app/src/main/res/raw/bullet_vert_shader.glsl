attribute vec4 a_Position;
attribute float a_Angle;
attribute float a_Alpha;

uniform float x_Scale;
uniform float y_Scale;

varying float v_Angle;
varying float v_Alpha;
varying float v_CosA;
varying float v_SinA;

void main()
{
	v_Angle = a_Angle;
	v_Alpha = a_Alpha;
	v_CosA = cos(a_Angle);
	v_SinA = sin(a_Angle);
	
	gl_PointSize = 14.0;
	gl_Position = vec4( a_Position.x, a_Position.y, 0.0, 1.0);
	
	gl_Position.x = a_Position.x * x_Scale;
	gl_Position.y = a_Position.y * y_Scale;
}