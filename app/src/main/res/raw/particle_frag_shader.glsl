precision mediump float;

uniform sampler2D u_Texture;

varying vec4 v_Color;
varying float v_ElapsedTime;
varying float v_RotationSpeed;

void main()
{
	float mid = 0.5;
	float vRotation = v_RotationSpeed;
    mediump vec2 rotated = vec2(cos(vRotation) * (gl_PointCoord.x - mid) + sin(vRotation) * (gl_PointCoord.y - mid) + mid,
                        cos(vRotation) * (gl_PointCoord.y - mid) - sin(vRotation) * (gl_PointCoord.x - mid) + mid);
	
	gl_FragColor = v_Color * texture2D(u_Texture, rotated); 
}