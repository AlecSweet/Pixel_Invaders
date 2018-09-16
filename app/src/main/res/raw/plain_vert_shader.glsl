
attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 a_TexCoordinate; 
 
uniform float x_Scale;
uniform float y_Scale;
uniform vec2 displacement;
uniform float mag;
//uniform float time;

varying vec2 v_TexCoordinate;
varying vec4 v_Color;
//varying float angle;
//varying vec2 v_displacement;
//varying vec2 position;
//varying vec2 v_scale;

void main()
{
	v_Color = a_Color;
	v_TexCoordinate = a_TexCoordinate;
	
	gl_Position = a_Position;
			
	gl_Position.x = (a_Position.x * mag + displacement.x) * x_Scale;
	gl_Position.y = (a_Position.y * mag + displacement.y) * y_Scale;
	
	//position = a_Position.xy;
	//v_displacement = gl_Position.xy;
	//v_scale = vec2(x_Scale, y_Scale);
	//v_displacement = displacement;
	/*angle = 10.0;
	if(mod(floor(time), 2.0) == 0.0)
	{
		angle *= pow(fract(time),2.0);
		angle += 5.28;
	}
	else
	{
		angle *= 1.0 - pow(fract(time),2.0);
		angle += 5.28;
	}*/
}