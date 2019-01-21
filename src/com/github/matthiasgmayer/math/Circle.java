package com.github.matthiasgmayer.math;

public class Circle {
	public Vector position;
	public float radius;
	public Circle(Vector position, float radius) {
		super();
		this.position = position;
		this.radius = radius;
	}
	public float distance(Vector v) {
		return Math.max(0, v.sub(position).length() - radius);
	}
}
