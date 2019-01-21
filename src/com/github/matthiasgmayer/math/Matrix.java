package com.github.matthiasgmayer.math;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Matrix implements Serializable{
	private static final long serialVersionUID = -1175253480457906361L;
	float[][] m;
	public final int width, height;

	public Matrix(float[]... fs) {
		this(fs[0].length, fs.length);
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].length != height)
				throw new ArithmeticException();
			m[i] = fs[i];
		}
	}

	public Matrix(int height, int width) {
		this.width = width;
		this.height = height;
		m = new float[width][height];
	}
	
	public static Matrix fromRows(float[]... fs) {
		float[][] f = new float[fs[0].length][fs.length];
		for (int i = 0; i < f.length; i++) {
			for (int j = 0; j < f[i].length; j++) {
				f[i][j] = fs[j][i];
			}
		}
		return new Matrix(f);
	}

	public float[] getRow(int i) {
		float[] f = new float[width];
		for (int j = 0; j < f.length; j++) {
			f[j] = m[j][i];
		}
		return f;
	}

	public float[] getColumn(int i) {
		return m[i];
	}
	public void setColumn(int i, float[] f) {
		m[i] = f;
	}

	protected Matrix doOperation(Matrix a, BiFunction<Float, Float, Float> f) {
		if (width != a.width || height != a.height)
			throw new ArithmeticException("Operands are not equal in Dimension");
		float[][] e = new float[m.length][m[0].length];
		for (int i = 0; i < e.length; i++) {
			for (int j = 0; j < e[i].length; j++) {
				e[i][j] = f.apply(m[i][j], a.m[i][j]);
			}
		}
		return new Matrix(e);
	}

	protected Matrix doOperation(Function<Float, Float> f) {
		float[][] e = new float[m.length][m[0].length];
		for (int i = 0; i < e.length; i++) {
			for (int j = 0; j < e[i].length; j++) {
				e[i][j] = f.apply(m[i][j]);
			}
		}
		return new Matrix(e);
	}

	public Matrix multiply(Matrix m) {
		if (width != m.height)
			throw new ArithmeticException("Matrix width is not equal to the other Matrix height");
		if (m.width == 1) {
			if (height == 1) {
				float[] f = getRow(0), f2 = m.getColumn(0), sum = new float[] { 0 };
				for (int i = 0; i < f2.length; i++) {
					sum[0] += f[i] * f2[i];
				}
				return new Matrix(sum);
			} else {
				float[] f = new float[height];
				for (int i = 0; i < f.length; i++) {
					f[i] = fromRows(getRow(i)).multiply(m).m[0][0];
				}
				return new Matrix(f);
			}
		} else {
			float[][] f = new float[m.width][];
			for (int i = 0; i < f.length; i++) {
				f[i] = multiply(new Matrix(m.getColumn(i))).getColumn(0);
			}
			return new Matrix(f);
		}

	}

	public Matrix add(Matrix m) {
		return doOperation(m, (a, b) -> a + b);
	}

	public Matrix neg() {
		return scale(-1f);
	}

	public Matrix sub(Matrix m) {
		return add(m.neg());
	}

	public Matrix scale(float f) {
		return doOperation(a -> a * f);
	}
	public Vector multiply(Vector v) {
		return new Vector(multiply((Matrix)v));
	}

	public Matrix solveGaussian() {
		LinkedList<Matrix> list = new LinkedList<>();
		list.add(this);

		int count = 0;
		
		while (list.get(0).height > 1) {
			Matrix m = list.get(0);
			Matrix m2 = m.withZeroAt(width - 2 - (count++ % (width - 2)+1));
			if(!m.equals(m2)) {
				list.add(0,m2);
			}else {
				list.remove(0);
				for (int i = 0; i < m.height; i++) {
					list.add(0,fromRows(m.getRow(i)));
				}
			}
		}
		Matrix solution = new Matrix(width, width - 1);

		int[] index = new int[list.size()];
		int row = 0;
		for (int i = 0; i < list.size(); i++) {
			Matrix m = fromRows(list.get(i).getRow(row));
			for (int j = 0; j < i; j++) {
				if(index[j] == -1) continue;
				m = m.substituteAt(index[j], fromRows(solution.getColumn(index[j])));
			}
			index[i] = m.findFirstNonNull();
			if(index[i] == -1) 
			{
				row++;
				i--;
				continue;
			}
			row = 0;
			if(index[i] == width - 1) return null;
			float[] sol = m.withOneAt(index[i]).getRow(0);
			sol[index[i]] = 0;
			for (int j = 0; j < sol.length - 1; j++) {
				sol[j] = -sol[j];
			}
			solution.setColumn(index[i], sol);
		}
		
		//switch order last non parameter to top
		for (int i = 0; i < solution.m.length; i++) {
			float temp = solution.m[i][width - 1];
			solution.m[i][width-1] = solution.m[i][0];
			solution.m[i][0] = temp;
		}
		return solution;

	}
	
	private int findFirstNonNull() {
		float[] f = getRow(0);
		for (int i = 0; i < width;i++) {
			if(f[i] != 0) return i;
		}
		return -1;
	}

	private Matrix substituteAt(int i, Matrix m) {
		Matrix[] rows = new Matrix[height];
		for (int j = 0; j < rows.length; j++) {
			rows[j] = fromRows(getRow(j));
			rows[j] = rows[j].add(m.scale(-rows[j].m[i][0]));
			rows[j].m[i][0] = 0;
		}
		return fromRows(rows);
	}

	private Matrix withOneAt(int i) {
		if (height != 1)
			throw new ArithmeticException("Matrix height not 1");
		return scale(1 / m[i][0]);
	}

	private Matrix withZeroAt(int i) {
		Matrix[] rows = new Matrix[height];
		boolean[] zeros = new boolean[height];
		for (int j = 0; j < rows.length; j++) {
			rows[j] = fromRows(getRow(j));
			zeros[j] = rows[j].m[i][0] == 0;
		}
		int count = 0;
		for (int j = 0; j < zeros.length; j++) {
			if (!zeros[j])
				count++;
		}
		if (count <= 1)
			return fromRows(rows); // TODO: if only one equation contains a zero;
		Matrix[] retRows = new Matrix[height - 1];
		count = 0;
		Matrix addRow = null;
		for (int j = 0; j < zeros.length; j++) {
			if (zeros[j]) {
				retRows[count++] = rows[j];
			} else if (addRow == null) {
				addRow = rows[j];
			} else {
				retRows[count++] = rows[j].add(addRow.scale(-rows[j].m[i][0] / addRow.m[i][0]));

			}
		}
		return fromRows(retRows);
	}

	private Matrix fromRows(Matrix[] m) {
		float[][] rows = new float[m.length][m[0].width];
		for (int i = 0; i < rows.length; i++) {
			rows[i] = m[i].getRow(0);
		}
		return fromRows(rows);
	}

	public boolean equals(Matrix m) {
		for (int i = 0; i < this.m.length; i++) {
			for (int j = 0; j < this.m[i].length; j++) {
				if (this.m[i][j] != m.m[i][j])
					return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String res = "";
		for (int i = 0; i < height; i++) {
			String s = "|";
			float[] f = getRow(i);
			for (int j = 0; j < f.length; j++) {
				s += Float.toString((Math.round(f[j] * 1000f)) / 1000f) + (j == f.length - 1 ? "" : ", ");
			}
			res += s + "|\n";
		}
		return res;
	}
	
	public static Matrix createRotation2D(float angle) {
		return fromRows(new float[] {(float)Math.cos(angle),(float)-Math.sin(angle)},
						new float[] {(float)Math.sin(angle),(float)Math.cos(angle)});
	}

}
