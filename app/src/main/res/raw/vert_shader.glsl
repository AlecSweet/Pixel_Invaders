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
uniform vec3 riftData;

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
	
	float radius = riftData.z;
	vec2 center = riftData.xy;
	vec2 tc = (rotated * mag) + posData.xy;
	tc -= center;
	float dist = length(tc);
	if (dist < radius) 
	{
		float angle = 20.0;
		if(mod(floor(time), 2.0) == 0.0)
		{
			angle *= pow(fract(time),2.0);
			angle += 6.28;
		}
		else
		{
			angle *= 1.0 - pow(fract(time),2.0);
			angle += 6.28;
		}
		float percent = (radius - dist) / radius;
		
		float theta = pow(percent,2.0) * angle;
		float s = sin(theta);
		float c = cos(theta);
		tc = vec2(dot(tc, vec2(c, -s)), dot(tc, vec2(s, c)));
		
		dist = length(tc);
		percent = (radius - dist) / radius;
		gl_PointSize = pointSize * ((percent * percent) + 1.0);
		v_Color.b *= 1.0 + percent * percent * 2.0;
			/*if(percent < 0.5)
			{
				v_Color.r *= 1.0 + (0.5 - percent) * 2.0;
				//v_Color.b -= (0.5 - percent) * 0.5;
			}
			else
			{
				v_Color.b *= 1.0 + (percent - 0.5) * 2.0;
				//v_Color.r -= (percent - 0.5) * 0.5;
			}*/
	}
	tc += center;
	  
	
	if(dist >= radius)
	{
		gl_Position.x = ((rotated.x * mag) + posData.x - x_ScreenShift) * x_Scale;
		gl_Position.y = ((rotated.y * mag) + posData.y - y_ScreenShift) * y_Scale;
		//gl_Position.x = (tc.x - x_ScreenShift) * x_Scale;
		//gl_Position.y = (tc.y - y_ScreenShift) * y_Scale;
	}
	else
	{
		gl_Position.x = (tc.x - x_ScreenShift) * x_Scale;
		gl_Position.y = (tc.y - y_ScreenShift) * y_Scale;
		//gl_Position.x = ((rotated.x * mag) + posData.x - x_ScreenShift) * x_Scale;
		//gl_Position.y = ((rotated.y * mag) + posData.y - y_ScreenShift) * y_Scale;
	}
	
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
}



