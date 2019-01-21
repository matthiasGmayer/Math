package com.github.matthiasgmayer.math;

import java.io.Serializable;

public class Vector extends Matrix implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1202457470167872938L;

	public static final Vector left2D = new Vector(-1, 0), right2D = new Vector(1, 0), up2D = new Vector(0, 1),
			down2D = new Vector(0, 1);

	public float[] m;

	public Vector(float... fs) {
		super(fs);
		m = super.m[0];
		if (fs.length > 0)
			x = fs[0];
		else
			x = null;
		if (fs.length > 1)
			y = fs[1];
		else
			y = null;
		if (fs.length > 2)
			z = fs[2];
		else
			z = null;
		if (fs.length > 3)
			w = fs[3];
		else
			w = null;
	}

	public final Float x, y, z, w;

	public Vector(Matrix m) {
		this(m.m[0]);
	}

	private float sum(float[] f) {
		float s = 0;
		for (float fl : f)
			s += fl;
		return s;
	}

	public int size() {
		return m.length;
	}

	public Vector add(Vector v) {
		return new Vector(super.add(v));
	}

	public Vector neg() {
		return new Vector(super.neg());
	}

	public Vector sub(Vector v) {
		return new Vector(super.sub(v));
	}

	public float lengthSquared() {
		return sum(doOperation(f -> f * f).getColumn(0));
	}

	public float length() {
		return (float) Math.sqrt(lengthSquared());
	}

	public Vector scale(float f) {
		return new Vector(super.scale(f));
	}

	public Vector norm(float f) {
		return scale(f / length());
	}

	public float dot(Vector v) {
		return Matrix.fromRows(m).multiply(v).m[0];
	}

	public float angle(Vector v) {
		return (float) Math.acos(dot(v) / length() / v.length());
	}

	public Vector cross(Vector v) {
		if (m.length != 3)
			throw new ArithmeticException();
		return new Vector(m[1] * v.m[2] - m[2] * v.m[1], m[2] * v.m[0] - m[0] * v.m[2], m[0] * v.m[1] - m[1] * v.m[0]);
	}

	public Vector norm() {
		return norm(1f);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector)) {
			return false;
		}
		Vector v = (Vector) obj;
		if (v.height != height)
			return false;
		for (int i = 0; i < m.length; i++) {
			if (v.m[i] != m[i])
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return m.hashCode();
	}
}
