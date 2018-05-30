precision mediump float;

uniform sampler2D u_Texture;

varying float v_Angle;
varying float v_Alpha;
varying float v_CosA;
varying float v_SinA;

void main()
{
	float mid = 0.5;

	mediump vec2 rotated = vec2(v_CosA * (gl_PointCoord.x - mid) + v_SinA * (gl_PointCoord.y - mid) + mid,
						v_CosA * (gl_PointCoord.y - mid) - v_SinA * (gl_PointCoord.x - mid) + mid);
	
	gl_FragColor = texture2D(u_Texture, rotated);
	gl_FragColor.a = gl_FragColor.a * v_Alpha;
}
