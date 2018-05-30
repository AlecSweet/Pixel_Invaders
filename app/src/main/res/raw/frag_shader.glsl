precision mediump float;

uniform sampler2D u_Texture;

varying vec4 v_Color;
varying float v_Angle;
varying float v_CosA;
varying float v_SinA;

void main()
{
	float mid = 0.5;
	gl_FragColor = v_Color;
	mediump vec2 rotated = vec2(v_CosA * (gl_PointCoord.x - mid) + v_SinA * (gl_PointCoord.y - mid) + mid,
							v_CosA * (gl_PointCoord.y - mid) - v_SinA * (gl_PointCoord.x - mid) + mid);
	gl_FragColor = v_Color * texture2D(u_Texture, rotated);
}