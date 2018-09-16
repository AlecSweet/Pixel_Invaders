precision mediump float;

uniform sampler2D u_Texture;
//uniform vec3 riftData;

varying vec4 v_Color;
varying vec2 v_TexCoordinate;
//varying float angle;
//varying vec2 v_displacement;
//varying vec2 position;
//varying vec2 v_scale;

void main()
{
	gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
	/*float radius = riftData.z;
	vec2 center = riftData.xy;
	
	vec2 tc = (position);
	tc -= center;
	float dist = length(tc);
	if (dist < radius) 
	{
		float percent = (radius - dist) / radius;
		float theta = pow(percent,2.0) * angle;
		float s = sin(theta);
		float c = cos(theta);
		tc = vec2(dot(tc, vec2(c, -s)), dot(tc, vec2(s, c)));
		tc += center;
		gl_FragColor.rgb = texture2D(u_Texture, tc).rgb;
	}*/
}
