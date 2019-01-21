package com.github.matthiasgmayer.math;

public class Line {
	public Vector anchor, direction;
	
	public final boolean segmented;

	public Line(Vector anchor, Vector direction, boolean segmented) {
		super();
		this.anchor = anchor;
		this.direction = direction;
		this.segmented = segmented;
	}
	public Line(Vector anchor, Vector direction) {
		this(anchor,direction,true);
	}
	public static Line fromPoints(Vector a, Vector b, boolean segmented) {
		return new Line(a, b.sub(a), segmented);
	}
	public static Line fromPoints(Vector a, Vector b) {
		return fromPoints(a,b,true);
	}
	public float distance(Vector p) {
		Vector n = direction.norm(-1f) , a = anchor;
		Vector s = a.sub(p);
		float scale = s.dot(n);
		if(segmented)
			scale = Math.max(0, Math.min(scale, direction.length()));
		Vector e = s.sub(n.scale(scale));
		return e.length();
	}
	public Vector getIntersection(Line l) {
		Matrix m = new Matrix(direction.m, l.direction.neg().m, l.anchor.sub(anchor).m);
		Matrix solve = m.solveGaussian();
		if(solve == null) return null;
		float r = solve.m[0][0], s = solve.m[1][0];
		if(segmented && (r < 0 || r > 1 || s < 0 || s > 1)) return null;
		Vector solution = anchor.add(direction.scale(r));
		return solution;
	}
}
