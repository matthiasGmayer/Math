package com.github.matthiasgmayer.math;

public class Rectangle {
	public Vector position;
	public Vector dimension;
	private Line[] edges;
	private Vector[] corners;
	public Rectangle(Vector position, Vector dimension) {
		super();
		this.position = position;
		this.dimension = dimension;
	}
	
	public Vector[] getCorners() {
		return corners == null ? calculateCorners() : corners;
	}
	private Vector[] calculateCorners() {
		corners = new Vector[4];
		for (int i = 0; i < edges.length; i++) {
			corners[i] = position.add(new Vector(dimension.x/2 * (i % 2 * 2 - 1), dimension.y/2 * ((i/2) % 2 * 2 - 1)));
		}
		return corners;
	}
	public Line[] getEdges() {
		return edges == null ? calculateEdges() : edges;
	}
	private Line[] calculateEdges() {
		edges = new Line[4];
		for (int i = 0; i < corners.length; i++) {
			edges[i] = Line.fromPoints(corners[i], corners[(i+1)%corners.length]);
		}
		return edges;
	}
	
	public float distanceSquared(Vector v) {
		float dx = Math.max(Math.abs(position.x - v.x) - dimension.x / 2f, 0);
		float dy = Math.max(Math.abs(position.y - v.y) - dimension.y / 2f, 0);
		return dx * dx + dy * dy;
	}
	public float distance(Vector v) {
		return (float)Math.sqrt(distanceSquared(v));
	}
	
	
}
