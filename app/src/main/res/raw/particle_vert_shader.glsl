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
		gl_Position.x = ((a_Position.x + distance * cos(a_Angle) - x_ScreenShift) * x_Scale);
		gl_Position.y = ((a_Position.y + distance * sin(a_Angle) - y_ScreenShift) * y_Scale);
	}
	else
	{
		v_Color.a = 0.0;
		gl_Position.x = 0.0;
		gl_Position.y = 0.0;
	}

}