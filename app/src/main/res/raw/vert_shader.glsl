precision highp float;

attribute highp vec4 a_Position;
attribute vec4 a_Color;
attribute float alpha;

uniform float x_ScreenShift;
uniform float y_ScreenShift;
uniform float x_Scale;
uniform float y_Scale;
uniform vec4 posData;
uniform float tilt;
uniform float mag;
uniform float squareLength;
uniform vec2 shadingPoint;
uniform float pointSize;
uniform float time;

varying vec4 v_Color;
varying highp float v_CosA;
varying highp float v_SinA;

void main()
{
	v_Color = a_Color;
	v_Color.a = alpha;
	v_CosA = posData.z;
	v_SinA = posData.w;

	float lightF = a_Position.z;
	if(alpha > 0.009 && alpha < 0.011)
	{ 
		float rNum = fract(sin(dot(vec2(a_Position.x * time, a_Position.y * time) ,vec2(12.9898,78.13)))*58.1);
		//float rNum = (sin(dot(vec2(a_Position.x * a_Position.y, time) ,vec2(12.9898,78.233))*fract(time))+1.0) / 2.0;
		
		if(a_Position.w == 0.0)
		{		
			v_Color = vec4( 1.0, rNum * 0.8, 0.0, 1.0);
		}
		else if(a_Position.w == 1.0)
		{		
			v_Color = vec4( rNum*0.4 + 0.6, 0.0, rNum*0.4 + 0.6, 1.0);
		}
		else if(a_Position.w == 2.0)
		{		
			if(rNum > 0.6)
			{
				v_Color = vec4( 0.0, rNum, rNum, 1.0);
			}
		}
		lightF = .9f;
	}
	gl_PointSize = pointSize;
	gl_Position = vec4( a_Position.x, a_Position.y, 0.0, 1.0);
	
	float tempY  = cos(tilt) * a_Position.y;
	
	float tY = tempY;
	float tX = a_Position.x;
	
	vec2 rotated = vec2(v_CosA * tX + v_SinA * tY,
						v_CosA * tY - v_SinA * tX);
	
	float normX = -rotated.x;
	float normY = rotated.y;
	float a = 1.1;
		
	float tLeft = -normX * 0.891 + normY * 0.454;
	float tRight = normX * 0.454 + normY * 0.891;
	float factor = (0.8 + 0.2 * lightF);
	float inc = squareLength/40.0;
	float bound = squareLength/2.0;
	
	for(float i = 0.0; i < bound; i += inc )
	{
		if(tLeft < 0.5 * pow(tRight + i, 2.0)- i)
		{
			v_Color.r *= factor;
			v_Color.g *= factor;
			v_Color.b *= factor;
		}
	}

	gl_Position.x = ((rotated.x * mag) + posData.x - x_ScreenShift) * x_Scale;
	gl_Position.y = ((rotated.y * mag) + posData.y - y_ScreenShift) * y_Scale;
}



