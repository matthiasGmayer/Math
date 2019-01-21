package com.github.matthiasgmayer.math;

public class Maths {
	public static float getAngle2D(Vector v, Vector v2) {
		Vector dis = v.sub(v2);
		return (float)(Math.atan(dis.y/dis.x) + (dis.x < -0 ? Math.PI : 0));
	}
	public static float getAngle2D(float x, float y, float x2, float y2) {
		return getAngle2D(new Vector(x,y), new Vector(x2,y2));
	}
	/**
	 * 
	 * 
	 * @param m
	 * @return returns a Matrix from a Matrix with parameters x1, x2 ... where the solution is expressed as |a, b(x1), c(x2),....|
	 */
//	public static Matrix solveMatrix(Matrix m) {
//	}
}
