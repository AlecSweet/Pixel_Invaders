precision highp float;

attribute highp vec4 a_Position;
attribute vec4 a_Color;

uniform float x_ScreenShift;
uniform float y_ScreenShift;
uniform float x_Scale;
uniform float y_Scale;
uniform float x_displacement;
uniform float y_displacement;
uniform highp float angle;
uniform float tilt;
uniform float mag;
uniform float squareLength;
uniform vec2 shadingPoint;
uniform float pointSize;

varying vec4 v_Color;
varying highp float v_Angle;
varying highp float v_CosA;
varying highp float v_SinA;

void main()
{
	v_Color = a_Color;
	v_Angle = angle;
	v_CosA = cos(v_Angle);
	v_SinA = sin(v_Angle);

	gl_PointSize = pointSize;
	gl_Position = vec4( a_Position.x, a_Position.y, 0.0, 1.0);
	
	float tempY  = cos(tilt) * a_Position.y;
	
	highp vec2 rotated = vec2(v_CosA * a_Position.x + v_SinA * tempY,
						v_CosA * tempY - v_SinA * a_Position.x);
	
	float normX = -rotated.x;
	float normY = rotated.y;
	float a = 1.1;
		
	float tLeft = -normX * sin(a) + normY * cos(a);
	float tRight = normX * cos(a)+ normY * sin(a);
	float factor = (0.8 + 0.2 * a_Position.z);
	
	for(float i = 0.0; i < squareLength/2.0; i+=squareLength/40.0)
	{
		if(tLeft < 0.5 * pow(tRight + i, 2.0)- i)
		{
			v_Color.r *= factor;
			v_Color.g *= factor;
			v_Color.b *= factor;
		}
	}

	gl_Position.x = ((rotated.x * mag) + x_displacement - x_ScreenShift) * x_Scale;
	gl_Position.y = ((rotated.y * mag) + y_displacement - y_ScreenShift) * y_Scale;
	
}



