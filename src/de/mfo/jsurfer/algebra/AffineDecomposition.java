/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mfo.jsurfer.algebra;

import javax.vecmath.*;

/**
 *
 * @author stussak
 */
public class AffineDecomposition
{
    static public class AffineParts
    {
        public Vector3d t;	/* Translation components */
        public Quat4d   q;	/* Essential rotation	  */
        public Quat4d   u;	/* Stretch rotation	  */
        public Vector3d k;	/* Stretch factors	  */
        public double f;	/* Sign of determinant	  */

        public String toString()
        {
            String result = "";
            result += "t=" + t + "\n";
            result += "q=" + q + "\n";
            result += "u=" + u + "\n";
            result += "k=" + k + "\n";
            result += "f=" + f;
            return result;
        }
    }

    public static AffineParts decompAffine( Matrix4d A )
    {
        AffineDecompositionFromCCode.AffineParts ap = new AffineDecompositionFromCCode.AffineParts();
        AffineDecompositionFromCCode.decomp_affine( toHMatrix( A ), ap );
        AffineParts result = new AffineParts();
        result.t = toVector3d( ap.t );
        result.q = toQuat4d( ap.q );
        result.u = toQuat4d( ap.u );
        result.k = toVector3d( ap.k );
        result.f = ap.f;
        return result;
    }

    static AffineDecompositionFromCCode.HMatrix toHMatrix( Matrix4d A )
    {
        double[][] m = new double[ 4 ][ 4 ];
        A.getRow( 0, m[ 0 ] );
        A.getRow( 1, m[ 1 ] );
        A.getRow( 2, m[ 2 ] );
        A.getRow( 3, m[ 3 ] );
        return new AffineDecompositionFromCCode.HMatrix( m );
    }

    static Quat4d toQuat4d( AffineDecompositionFromCCode.Quat q )
    {
        return new Quat4d( q.x, q.y, q.z, q.w );
    }

    static Vector3d toVector3d( AffineDecompositionFromCCode.HVect v )
    {
        return new Vector3d( v.x, v.y, v.z );
    }
}

class AffineDecompositionFromCCode {


    static class Quat
    {
        public double x, y, z, w;
        public Quat() {};
        public Quat( double x, double y, double z, double w ) { this.x = x; this.y = y; this.z = z; this.w = w; }
    }
    static class HVect extends Quat
    {
        public HVect() { super(); }
        public HVect( double x, double y, double z, double w ) { super( x,y,z,w ); }
    }
    static final int X = 0;
    static final int Y = 1;
    static final int Z = 2;
    static final int W = 3;

    static class HMatrix
    {
        public double[][] m;
        public HMatrix() { m = new double[ 4 ][ 4 ]; }
        public HMatrix( double[][] m ) { this.m = m; }
    }

    static class AffineParts
    {
        HVect t;	/* Translation components */
        Quat  q;	/* Essential rotation	  */
        Quat  u;	/* Stretch rotation	  */
        HVect k;	/* Stretch factors	  */
        double f;	/* Sign of determinant	  */

        public AffineParts()
        {
            t = new HVect();
            q = new Quat();
            u = new Quat();
            k = new HVect();
        }
    }

    /******* Matrix Preliminaries *******/

    /** Fill out 3x3 matrix to 4x4 **/
    static void mat_pad( HMatrix A ) { A.m[W][X]=A.m[X][W]=A.m[W][Y]=A.m[Y][W]=A.m[W][Z]=A.m[Z][W]=0; A.m[W][W]=1; }

    /** Copy nxn matrix A to C using "=" for assignment **/
    static void mat_copy_assign( HMatrix C, HMatrix A, int n ) { int i,j; for(i=0;i<n;i++) for(j=0;j<n;j++) C.m[i][j] = (A.m[i][j]);}

    /** Copy nxn matrix A to C using "-=" for assignment **/
    static void mat_copy_minus( HMatrix C, HMatrix A, int n ) { int i,j; for(i=0;i<n;i++) for(j=0;j<n;j++) C.m[i][j] -= (A.m[i][j]);}

    /** Copy nxn matrix A to C using "= -" for assignment **/
    static void mat_copy_neg( HMatrix C, HMatrix A, int n ) { int i,j; for(i=0;i<n;i++) for(j=0;j<n;j++) C.m[i][j] = -(A.m[i][j]);}

    /** Copy transpose of nxn matrix A to C using "=" for assignment **/
    static void mat_tpose( HMatrix AT, HMatrix A, int n ) {int i,j; for(i=0;i<n;i++) for(j=0;j<n;j++) AT.m[i][j] = (A.m[j][i]);}

    /** Assign nxn matrix C the element-wise addition of A and B **/
    static void mat_scale_add( HMatrix C, double as, HMatrix A, double bs, HMatrix B, int n ) {int i,j; for(i=0;i<n;i++) for(j=0;j<n;j++) C.m[i][j] = (as*A.m[i][j]) + (bs*B.m[i][j]); }

    /** Multiply the upper left 3x3 parts of A and B to get AB **/
    static void mat_mult(HMatrix A, HMatrix B, HMatrix AB)
    {
            int i, j;
            for (i=0; i<3; i++) for (j=0; j<3; j++)
                    AB.m[i][j] = A.m[i][0]*B.m[0][j] + A.m[i][1]*B.m[1][j] + A.m[i][2]*B.m[2][j];
    }

    /** Return dot product of length 3 vectors va and vb **/
    static double vdot(double[] va, double[] vb)
    {
            return (va[0]*vb[0] + va[1]*vb[1] + va[2]*vb[2]);
    }

    /** Set v to cross product of length 3 vectors va and vb **/
    static void vcross(double[] va, double[] vb, double[] v)
    {
            v[0] = va[1]*vb[2] - va[2]*vb[1];
            v[1] = va[2]*vb[0] - va[0]*vb[2];
            v[2] = va[0]*vb[1] - va[1]*vb[0];
    }

    /** Set MadjT to transpose of inverse of M times determinant of M **/
    static void adjoint_transpose(HMatrix M, HMatrix MadjT)
    {
            vcross(M.m[1], M.m[2], MadjT.m[0]);
            vcross(M.m[2], M.m[0], MadjT.m[1]);
            vcross(M.m[0], M.m[1], MadjT.m[2]);
    }

    /******* Quaternion Preliminaries *******/

    /* Construct a (possibly non-unit) quaternion from real components. */
    static Quat Qt_(double x, double y, double z, double w)
    {
            Quat qq = new Quat();
            qq.x = x;
            qq.y = y;
            qq.z = z;
            qq.w = w;
            return (qq);
    }

    /* Return conjugate of quaternion. */
    static Quat Qt_Conj(Quat q)
    {
            Quat qq = new Quat();
            qq.x = -q.x;
            qq.y = -q.y;
            qq.z = -q.z;
            qq.w = q.w;
            return (qq);
    }

    /* Return quaternion product qL * qR.  Note: order is important!
     * To combine rotations, use the product Mul(qSecond, qFirst),
     * which gives the effect of rotating by qFirst then qSecond. */
    static Quat Qt_Mul(Quat qL, Quat qR)
    {
            Quat qq = new Quat();
            qq.w = qL.w*qR.w - qL.x*qR.x - qL.y*qR.y - qL.z*qR.z;
            qq.x = qL.w*qR.x + qL.x*qR.w + qL.y*qR.z - qL.z*qR.y;
            qq.y = qL.w*qR.y + qL.y*qR.w + qL.z*qR.x - qL.x*qR.z;
            qq.z = qL.w*qR.z + qL.z*qR.w + qL.x*qR.y - qL.y*qR.x;
            return (qq);
    }

    /* Return product of quaternion q by scalar w. */
    static Quat Qt_Scale(Quat q, double w)
    {
            Quat qq = new Quat();
            qq.w = q.w*w;
            qq.x = q.x*w;
            qq.y = q.y*w;
            qq.z = q.z*w;
            return (qq);
    }

static Quat Qt_FromMatrix(HMatrix mat)
{
 Quat qu = new Quat();
 double tr, s;

 tr = mat.m[X][X] + mat.m[Y][Y]+ mat.m[Z][Z];
 if (tr >= 0.0) {
  s = Math.sqrt(tr + mat.m[W][W]);
  qu.w = s*0.5;
  s = 0.5 / s;
  qu.x = (mat.m[Z][Y] - mat.m[Y][Z]) * s;
  qu.y = (mat.m[X][Z] - mat.m[Z][X]) * s;
  qu.z = (mat.m[Y][X] - mat.m[X][Y]) * s;
 } else {
  int h = X;
  if (mat.m[Y][Y] > mat.m[X][X]) h = Y;
  if (mat.m[Z][Z] > mat.m[h][h]) h = Z;
  switch (h) {
   case X: s = Math.sqrt( (mat.m[X][X] - (mat.m[Y][Y]+mat.m[Z][Z])) + mat.m[W][W] ); qu.x = s*0.5; s = 0.5 / s; qu.y = (mat.m[X][Y] + mat.m[Y][X]) * s; qu.z = (mat.m[Z][X] + mat.m[X][Z]) * s; qu.w = (mat.m[Z][Y] - mat.m[Y][Z]) * s; break;
   case Y: s = Math.sqrt( (mat.m[Y][Y] - (mat.m[Z][Z]+mat.m[X][X])) + mat.m[W][W] ); qu.y = s*0.5; s = 0.5 / s; qu.z = (mat.m[Y][Z] + mat.m[Z][Y]) * s; qu.x = (mat.m[X][Y] + mat.m[Y][X]) * s; qu.w = (mat.m[X][Z] - mat.m[Z][X]) * s; break;
   case Z: s = Math.sqrt( (mat.m[Z][Z] - (mat.m[X][X]+mat.m[Y][Y])) + mat.m[W][W] ); qu.z = s*0.5; s = 0.5 / s; qu.x = (mat.m[Z][X] + mat.m[X][Z]) * s; qu.y = (mat.m[Y][Z] + mat.m[Z][Y]) * s; qu.w = (mat.m[Y][X] - mat.m[X][Y]) * s; break;
  }
 }
 if (mat.m[W][W] != 1.0) qu = Qt_Scale(qu, 1/Math.sqrt(mat.m[W][W]));
 return (qu);
}

    /******* Decomp Auxiliaries *******/
    static final HMatrix mat_id;
    static
    {
        double[][] id =
        {
            {1,0,0,0},
            {0,1,0,0},
            {0,0,1,0},
            {0,0,0,1}
        };
        mat_id = new HMatrix( id );
    }

    /** Compute either the 1 or infinity norm of M, depending on tpose **/
    static double mat_norm(HMatrix M, int tpose)
    {
            int i;
            double sum, max;
            max = 0.0;
            for (i=0; i<3; i++) {
                    if (tpose != 1 ) sum = Math.abs(M.m[0][i])+Math.abs(M.m[1][i])+Math.abs(M.m[2][i]);
                    else	sum = Math.abs(M.m[i][0])+Math.abs(M.m[i][1])+Math.abs(M.m[i][2]);
                    if (max<sum) max = sum;
            }
            return max;
    }

    static double norm_inf(HMatrix M) {
            return mat_norm(M, 0);
    }
    static double norm_one(HMatrix M) {
            return mat_norm(M, 1);
    }

    /** Return index of column of M containing maximum abs entry, or -1 if M=0 **/
    static int find_max_col(HMatrix M)
    {
            double abs, max;
            int i, j, col;
            max = 0.0;
            col = -1;
            for (i=0; i<3; i++) for (j=0; j<3; j++) {
                    abs = M.m[i][j];
                    if (abs<0.0) abs = -abs;
                    if (abs>max) {
                            max = abs;
                            col = j;
                    }
            }
            return col;
    }

    /** Setup u for Household reflection to zero all v components but first **/
    static void make_reflector(double[] v, double[] u)
    {
            double s = Math.sqrt(vdot(v, v));
            u[0] = v[0];
            u[1] = v[1];
            u[2] = v[2] + ((v[2]<0.0) ? -s : s);
            s = Math.sqrt(2.0/vdot(u, u));
            u[0] = u[0]*s;
            u[1] = u[1]*s;
            u[2] = u[2]*s;
    }

    /** Apply Householder reflection represented by u to column vectors of M **/
    static void reflect_cols(HMatrix M, double[] u)
    {
            int i, j;
            for (i=0; i<3; i++) {
                    double s = u[0]*M.m[0][i] + u[1]*M.m[1][i] + u[2]*M.m[2][i];
                    for (j=0; j<3; j++) M.m[j][i] -= u[j]*s;
            }
    }
    /** Apply Householder reflection represented by u to row vectors of M **/
    static void reflect_rows(HMatrix M, double[] u)
    {
            int i, j;
            for (i=0; i<3; i++) {
                    double s = vdot(u, M.m[i]);
                    for (j=0; j<3; j++) M.m[i][j] -= u[j]*s;
            }
    }

    /** Find orthogonal factor Q of rank 1 (or less) M **/
    static void do_rank1(HMatrix M, HMatrix Q)
    {
            double[] v1 = new double[3];
            double[] v2 = new double[3];
            double s;
            int col;
            mat_copy_assign(Q,mat_id,4);
            /* If rank(M) is 1, we should find a non-zero column in M */
            col = find_max_col(M);
            if (col<0) return; /* Rank is 0 */
            v1[0] = M.m[0][col];
            v1[1] = M.m[1][col];
            v1[2] = M.m[2][col];
            make_reflector(v1, v1);
            reflect_cols(M, v1);
            v2[0] = M.m[2][0];
            v2[1] = M.m[2][1];
            v2[2] = M.m[2][2];
            make_reflector(v2, v2);
            reflect_rows(M, v2);
            s = M.m[2][2];
            if (s<0.0) Q.m[2][2] = -1.0;
            reflect_cols(Q, v1);
            reflect_rows(Q, v2);
    }

/** Find orthogonal factor Q of rank 2 (or less) M using adjoint transpose **/
static void do_rank2(HMatrix M, HMatrix MadjT, HMatrix Q)
{
	double[] v1 = new double[3];
        double[] v2 = new double[3];
	double w, x, y, z, c, s, d;
	int i, j, col;
	/* If rank(M) is 2, we should find a non-zero column in MadjT */
	col = find_max_col(MadjT);
	if (col<0) {
		do_rank1(M, Q);
		return;
	} /* Rank<2 */
	v1[0] = MadjT.m[0][col];
	v1[1] = MadjT.m[1][col];
	v1[2] = MadjT.m[2][col];
	make_reflector(v1, v1);
	reflect_cols(M, v1);
	vcross(M.m[0], M.m[1], v2);
	make_reflector(v2, v2);
	reflect_rows(M, v2);
	w = M.m[0][0];
	x = M.m[0][1];
	y = M.m[1][0];
	z = M.m[1][1];
	if (w*z>x*y) {
		c = z+w;
		s = y-x;
		d = Math.sqrt(c*c+s*s);
		c = c/d;
		s = s/d;
		Q.m[0][0] = Q.m[1][1] = c;
		Q.m[0][1] = -(Q.m[1][0] = s);
	} else {
		c = z-w;
		s = y+x;
		d = Math.sqrt(c*c+s*s);
		c = c/d;
		s = s/d;
		Q.m[0][0] = -(Q.m[1][1] = c);
		Q.m[0][1] = Q.m[1][0] = s;
	}
	Q.m[0][2] = Q.m[2][0] = Q.m[1][2] = Q.m[2][1] = 0.0;
	Q.m[2][2] = 1.0;
	reflect_cols(Q, v1);
	reflect_rows(Q, v2);
}


/******* Polar Decomposition *******/

/* Polar Decomposition of 3x3 matrix in 4x4,
 * M = QS.  See Nicholas Higham and Robert S. Schreiber,
 * Fast Polar Decomposition of An Arbitrary Matrix,
 * Technical Report 88-942, October 1988,
 * Department of Computer Science, Cornell University.
 */
static double polar_decomp(HMatrix M, HMatrix Q, HMatrix S)
{
        final double TOL = 1.0e-6;
	HMatrix Mk = new HMatrix();
        HMatrix MadjTk = new HMatrix();
        HMatrix Ek = new HMatrix();
	double det, M_one, M_inf, MadjT_one, MadjT_inf, E_one, gamma, t1, t2, g1, g2;
	mat_tpose(Mk,M,3);
	M_one = norm_one(Mk);
	M_inf = norm_inf(Mk);
	do {
		adjoint_transpose(Mk, MadjTk);
		det = vdot(Mk.m[0], MadjTk.m[0]);
		if (det==0.0) {
			do_rank2(Mk, MadjTk, Mk);
			break;
		}
		MadjT_one = norm_one(MadjTk);
		MadjT_inf = norm_inf(MadjTk);
		gamma = Math.sqrt(Math.sqrt((MadjT_one*MadjT_inf)/(M_one*M_inf))/Math.abs(det));
		g1 = gamma*0.5;
		g2 = 0.5/(gamma*det);
		mat_copy_assign(Ek,Mk,3);
		mat_scale_add(Mk,g1,Mk,g2,MadjTk,3);
		mat_copy_minus(Ek,Mk,3);
		E_one = norm_one(Ek);
		M_one = norm_one(Mk);
		M_inf = norm_inf(Mk);
	} while (E_one>(M_one*TOL));
	mat_tpose(Q,Mk,3);
	mat_pad(Q);
	mat_mult(Mk, M, S);
	mat_pad(S);
	for ( int i=0; i<3; i++) for (int j=i; j<3; j++)
		S.m[i][j] = S.m[j][i] = 0.5*(S.m[i][j]+S.m[j][i]);
	return (det);
}

/******* Spectral Decomposition *******/

/* Compute the spectral decomposition of symmetric positive semi-definite S.
 * Returns rotation in U and scale factors in result, so that if K is a diagonal
 * matrix of the scale factors, then S = U K (U transpose). Uses Jacobi method.
 * See Gene H. Golub and Charles F. Van Loan. Matrix Computations. Hopkins 1983.
 */
static HVect spect_decomp( HMatrix S, HMatrix U )
{
	HVect kv = new HVect();
	double[] Diag = new double[3];
        double[] OffD = new double[3]; /* OffD is off-diag (by omitted index) */
	double g,h,fabsh,fabsOffDi,t,theta,c,s,tau,ta,OffDq,a,b;
	final byte[] nxt = {Y,Z,X};
	int sweep, i, j;
	mat_copy_assign(U,mat_id,4);
	Diag[X] = S.m[X][X];
	Diag[Y] = S.m[Y][Y];
	Diag[Z] = S.m[Z][Z];
	OffD[X] = S.m[Y][Z];
	OffD[Y] = S.m[Z][X];
	OffD[Z] = S.m[X][Y];
	for (sweep=20; sweep>0; sweep--) {
		double sm = Math.abs(OffD[X])+Math.abs(OffD[Y])+Math.abs(OffD[Z]);
		if (sm==0.0) break;
		for (i=Z; i>=X; i--) {
			int p = nxt[i];
			int q = nxt[p];
			fabsOffDi = Math.abs(OffD[i]);
			g = 100.0*fabsOffDi;
			if (fabsOffDi>0.0) {
				h = Diag[q] - Diag[p];
				fabsh = Math.abs(h);
				if (fabsh+g==fabsh) {
					t = OffD[i]/h;
				} else {
					theta = 0.5*h/OffD[i];
					t = 1.0/(Math.abs(theta)+Math.sqrt(theta*theta+1.0));
					if (theta<0.0) t = -t;
				}
				c = 1.0/Math.sqrt(t*t+1.0);
				s = t*c;
				tau = s/(c+1.0);
				ta = t*OffD[i];
				OffD[i] = 0.0;
				Diag[p] -= ta;
				Diag[q] += ta;
				OffDq = OffD[q];
				OffD[q] -= s*(OffD[p] + tau*OffD[q]);
				OffD[p] += s*(OffDq   - tau*OffD[p]);
				for (j=Z; j>=X; j--) {
					a = U.m[j][p];
					b = U.m[j][q];
					U.m[j][p] -= s*(b + tau*a);
					U.m[j][q] += s*(a - tau*b);
				}
			}
		}
	}
	kv.x = Diag[X];
	kv.y = Diag[Y];
	kv.z = Diag[Z];
	kv.w = 1.0;
	return (kv);
}


static double sgn( boolean n, double v ) { return (n?-(v):(v)); }
static void swap(double[] a, int i, int j ) {a[3]=a[i]; a[i]=a[j]; a[j]=a[3];}
static void cycle( double[] a, int p) { if (p != 0) {a[3]=a[0]; a[0]=a[1]; a[1]=a[2]; a[2]=a[3];} else {a[3]=a[2]; a[2]=a[1]; a[1]=a[0]; a[0]=a[3];} }

/******* Spectral Axis Adjustment *******/

/* Given a unit quaternion, q, and a scale vector, k, find a unit quaternion, p,
 * which permutes the axes and turns freely in the plane of duplicate scale
 * factors, such that q p has the largest possible w component, i.e. the
 * smallest possible angle. Permutes k's components to go with q p instead of q.
 * See Ken Shoemake and Tom Duff. Matrix Animation and Polar Decomposition.
 * Proceedings of Graphics Interface 1992. Details on p. 262-263.
 */
static Quat snuggle(Quat q, HVect k) {
        final double SQRTHALF = 0.7071067811865475244;
        Quat p = new Quat();
        double[] ka = new double[4];
        int i, turn = -1;
        ka[X] = k.x;
        ka[Y] = k.y;
        ka[Z] = k.z;
        if (ka[X] == ka[Y]) {
            if (ka[X] == ka[Z]) {
                turn = W;
            } else {
                turn = Z;
            }
        } else {
            if (ka[X] == ka[Z]) {
                turn = Y;
            } else if (ka[Y] == ka[Z]) {
                turn = X;
            }
        }
        if (turn >= 0) {
            Quat qtoz, qp;
            boolean[] neg = new boolean[3];
            int win;
            double[] mag = new double[3];
            double c, s, t;
            final Quat qxtoz = Qt_(0, SQRTHALF, 0, SQRTHALF);
            final Quat qytoz = Qt_(SQRTHALF, 0, 0, SQRTHALF);
            final Quat qppmm = Qt_(0.5, 0.5, -0.5, -0.5);
            final Quat qpppp = Qt_(0.5, 0.5, 0.5, 0.5);
            final Quat qmpmm = Qt_(-0.5, 0.5, -0.5, -0.5);
            final Quat qpppm = Qt_(0.5, 0.5, 0.5, -0.5);
            final Quat q0001 = Qt_(0.0, 0.0, 0.0, 1.0);
            final Quat q1000 = Qt_(1.0, 0.0, 0.0, 0.0);
            switch (turn) {
                default:
                    return (Qt_Conj(q));
                case X:
                    q = Qt_Mul(q, qtoz = qxtoz);
                    swap(ka, X, Z);
                    break;
                case Y:
                    q = Qt_Mul(q, qtoz = qytoz);
                    swap(ka, Y, Z);
                    break;
                case Z:
                    qtoz = q0001;
                    break;
            }
            q = Qt_Conj(q);
            mag[0] = (double) q.z * q.z + (double) q.w * q.w - 0.5;
            mag[1] = (double) q.x * q.z - (double) q.y * q.w;
            mag[2] = (double) q.y * q.z + (double) q.x * q.w;
            for (i = 0; i < 3; i++) {
                if (neg[i] = (mag[i] < 0.0)) {
                    mag[i] = -mag[i];
                }
            }
            if (mag[0] > mag[1]) {
                if (mag[0] > mag[2]) {
                    win = 0;
                } else {
                    win = 2;
                }
            } else {
                if (mag[1] > mag[2]) {
                    win = 1;
                } else {
                    win = 2;
                }
            }
            switch (win) {
                case 0:
                    if (neg[0]) {
                        p = q1000;
                    } else {
                        p = q0001;
                    }
                    break;
                case 1:
                    if (neg[1]) {
                        p = qppmm;
                    } else {
                        p = qpppp;
                    }
                    cycle(ka, 0);
                    break;
                case 2:
                    if (neg[2]) {
                        p = qmpmm;
                    } else {
                        p = qpppm;
                    }
                    cycle(ka, 1);
                    break;
            }
            qp = Qt_Mul(q, p);
            t = Math.sqrt(mag[win] + 0.5);
            p = Qt_Mul(p, Qt_(0.0, 0.0, -qp.z / t, qp.w / t));
            p = Qt_Mul(qtoz, Qt_Conj(p));
        } else {
            double[] qa = new double[4];
            double[] pa = new double[4];
            int lo, hi, par = 0;
            boolean[] neg = new boolean[4];
            double all, big, two;
            qa[0] = q.x;
            qa[1] = q.y;
            qa[2] = q.z;
            qa[3] = q.w;
            for (i = 0; i < 4; i++) {
                pa[i] = 0.0;
                if (neg[i] = (qa[i] < 0.0)) {
                    qa[i] = -qa[i];
                }
                par ^= neg[i] ? 1 : 0;
            }

            if (qa[0] > qa[1]) {
                lo = 0;
            } else {
                lo = 1;
            }
            if (qa[2] > qa[3]) {
                hi = 2;
            } else {
                hi = 3;
            }
            if (qa[lo] > qa[hi]) {
                if (qa[lo ^ 1] > qa[hi]) {
                    hi = lo;
                    lo ^= 1;
                } else {
                    hi ^= lo;
                    lo ^= hi;
                    hi ^= lo;
                }
            } else {
                if (qa[hi ^ 1] > qa[lo]) {
                    lo = hi ^ 1;
                }
            }
            all = (qa[0] + qa[1] + qa[2] + qa[3]) * 0.5;
            two = (qa[hi] + qa[lo]) * (0.7071067811865475244);
            big = qa[hi];
            if (all > two) {
                if (all > big) {
                    {
                        for (i = 0; i < 4; i++) {
                            pa[i] = ((neg[i]) ? -(0.5) : (0.5));
                        }
                    }
                    if (par != 0) {
                        ka[3] = ka[0];
                        ka[0] = ka[1];
                        ka[1] = ka[2];
                        ka[2] = ka[3];
                    } else {
                        ka[3] = ka[2];
                        ka[2] = ka[1];
                        ka[1] = ka[0];
                        ka[0] = ka[3];
                    }
                } else {
                    pa[hi] = ((neg[hi]) ? -(1.0) : (1.0));
                }
            } else {
                if (two > big) {
                    pa[hi] = ((neg[hi]) ? -((0.7071067811865475244)) : ((0.7071067811865475244)));
                    pa[lo] = ((neg[lo]) ? -((0.7071067811865475244)) : ((0.7071067811865475244)));
                    if (lo > hi) {
                        hi ^= lo;
                        lo ^= hi;
                        hi ^= lo;
                    }
                    if (hi == W) {
                        int[] code = {1, 2, 0};
                        hi = code[lo];
                        lo = 3 - hi - lo;
                    }
                    {
                        ka[3] = ka[hi];
                        ka[hi] = ka[lo];
                        ka[lo] = ka[3];
                    }
                } else {
                    pa[hi] = ((neg[hi]) ? -(1.0) : (1.0));
                }
            }
            p.x = -pa[0];
            p.y = -pa[1];
            p.z = -pa[2];
            p.w = pa[3];
        }
        k.x = ka[X];
        k.y = ka[Y];
        k.z = ka[Z];
        return (p);
    }



/******* Decompose Affine Matrix *******/

/* Decompose 4x4 affine matrix A as TFRUK(U transpose), where t contains the
 * translation components, q contains the rotation R, u contains U, k contains
 * scale factors, and f contains the sign of the determinant.
 * Assumes A transforms column vectors in right-handed coordinates.
 * See Ken Shoemake and Tom Duff. Matrix Animation and Polar Decomposition.
 * Proceedings of Graphics Interface 1992.
 */
static void decomp_affine(HMatrix A, AffineParts parts)
{
	HMatrix Q = new HMatrix();
	HMatrix S = new HMatrix();
	HMatrix U = new HMatrix();
	Quat p;
	double det;

	parts.t = new HVect(A.m[X][W], A.m[Y][W], A.m[Z][W], 0);
	det = polar_decomp(A, Q, S);
	if (det<0.0) {
		mat_copy_neg(Q,Q,3);
		parts.f = -1;
	} else parts.f = 1;
	parts.q = Qt_FromMatrix(Q);
	parts.k = spect_decomp(S, U);
	parts.u = Qt_FromMatrix(U);
	p = snuggle(parts.u, parts.k);
	parts.u = Qt_Mul(parts.u, p);
}

/******* Invert Affine Decomposition *******/

/* Compute inverse of affine decomposition.
 */
static void invert_affine(AffineParts parts, AffineParts inverse)
{
	Quat t = new Quat();
        Quat p = new Quat();
	inverse.f = parts.f;
	inverse.q = Qt_Conj(parts.q);
	inverse.u = Qt_Mul(parts.q, parts.u);
	inverse.k.x = (parts.k.x==0.0) ? 0.0 : 1.0/parts.k.x;
	inverse.k.y = (parts.k.y==0.0) ? 0.0 : 1.0/parts.k.y;
	inverse.k.z = (parts.k.z==0.0) ? 0.0 : 1.0/parts.k.z;
	inverse.k.w = parts.k.w;
	t = Qt_(-parts.t.x, -parts.t.y, -parts.t.z, 0);
	t = Qt_Mul(Qt_Conj(inverse.u), Qt_Mul(t, inverse.u));
	t = Qt_(inverse.k.x*t.x, inverse.k.y*t.y, inverse.k.z*t.z, 0);
	p = Qt_Mul(inverse.q, inverse.u);
	t = Qt_Mul(p, Qt_Mul(t, Qt_Conj(p)));
	inverse.t = (inverse.f>0.0) ? new HVect( t.x, t.y, t.z, t.w ) : new HVect(-t.x, -t.y, -t.z, 0);
}

}


