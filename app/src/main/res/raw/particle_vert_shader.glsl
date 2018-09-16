attribute vec2 a_Position;  
attribute vec4 a_Color;
attribute float a_Angle;
attribute float a_StartTime;
attribute float a_Speed;
attribute float a_MaxDistance;
attribute float a_RotationSpeed;

uniform float x_Scale;
uniform float y_Scale;
uniform float u_Time;
uniform float x_ScreenShift;
uniform float y_ScreenShift;
uniform float pointSize;
uniform vec3 riftData;

varying vec4 v_Color;
varying float v_ElapsedTime;
varying float v_RotationSpeed;

void main()
{
	v_Color = a_Color;
	
	v_ElapsedTime = u_Time - a_StartTime;
	
	float distance = a_Speed * v_ElapsedTime;
	
	float ratio = distance / a_MaxDistance;
	
	v_RotationSpeed = u_Time * a_RotationSpeed;
	
	gl_PointSize = pointSize;
	
	gl_Position = vec4( a_Position, 0.0, 1.0);
	
	if(ratio <= 1.0)
	{
		v_Color.a = (1.0 * a_Color.a) - (ratio * a_Color.a);
		
		
		float radius = riftData.z;
		
		//float angle = 6.28 * fract(u_Time) * sign;
		
		vec2 center = riftData.xy;
		vec2 tc = vec2(a_Position.x + distance * cos(a_Angle), a_Position.y + distance * sin(a_Angle));
		//vec2 tc = vec2(a_Position.x, a_Position.y);
		tc -= center;
		float dist = length(tc);
		if (dist < radius) 
		{
			float angle = 20.0;
			if(mod(floor(u_Time), 2.0) == 0.0)
			{
				angle *= pow(fract(u_Time),2.0);
				angle += 6.28;
			}
			else
			{
				angle *= 1.0 - pow(fract(u_Time),2.0);
				angle += 6.28;
			}
			float percent = (radius - dist) / radius;
			
			float theta = pow(percent,2.0)* angle;
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

		gl_Position.x = ((tc.x - x_ScreenShift) * x_Scale);
		gl_Position.y = ((tc.y - y_ScreenShift) * y_Scale);
		
		//gl_Position.x = ((a_Position.x + distance * cos(a_Angle) - x_ScreenShift) * x_Scale);
		//gl_Position.y = ((a_Position.y + distance * sin(a_Angle) - y_ScreenShift) * y_Scale);
	}
	else
	{
		v_Color.a = 0.0;
		gl_Position.x = 0.0;
		gl_Position.y = 0.0;
	}

}