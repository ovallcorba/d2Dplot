package com.vava33.d2dplot.auxi;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import org.apache.commons.math3.util.FastMath;

public class CircleFitter {
    /**
     * Test program entry point.
     * 
     * @param args command line arguments
     */

    /** Current circle center. */
    private final Point2D.Float center;

    /** Current circle radius. */
    private float rHat;

    /** Circular ring sample points. */
    private Point2D.Float[] points;

    /** Current cost function value. */
    private float J;

    /** Current cost function gradient. */
    private float dJdx;
    private float dJdy;

    /**
     * Build a new instance with a default current circle.
     */
    public CircleFitter() {
        this.center = new Point2D.Float(0.0f, 0.0f);
        this.rHat = 1.0f;
        this.points = null;
    }

    /**
     * Initialize an approximate circle based on all triplets.
     * 
     * @param points circular ring sample points
     * @exception LocalException if all points are aligned
     */
    public void initialize(Point2D.Float[] points) throws LocalException {

        // store the points array
        this.points = points;

        // analyze all possible points triplets
        this.center.x = 0.0f;
        this.center.y = 0.0f;
        int n = 0;
        for (int i = 0; i < (points.length - 2); ++i) {
            final Point2D.Float p1 = points[i];
            for (int j = i + 1; j < (points.length - 1); ++j) {
                final Point2D.Float p2 = points[j];
                for (int k = j + 1; k < points.length; ++k) {
                    final Point2D.Float p3 = points[k];

                    // compute the triangle circumcenter
                    final Point2D.Float cc = this.circumcenter(p1, p2, p3);
                    if (cc != null) {
                        // the points are not aligned, we have a circumcenter
                        ++n;
                        this.center.x += cc.x;
                        this.center.y += cc.y;
                    }
                }
            }
        }

        if (n == 0) {
            throw new LocalException("all points are aligned");
        }

        // initialize using the circumcenters average
        this.center.x /= n;
        this.center.y /= n;
        this.updateRadius();

    }

    /**
     * Update the circle radius.
     */
    private void updateRadius() {
        this.rHat = 0;
        for (final Float point : this.points) {
            final float dx = point.x - this.center.x;
            final float dy = point.y - this.center.y;
            this.rHat += FastMath.sqrt(dx * dx + dy * dy);
        }
        this.rHat /= this.points.length;
    }

    /**
     * Compute the circumcenter of three points.
     * 
     * @param pI first point
     * @param pJ second point
     * @param pK third point
     * @return circumcenter of pI, pJ and pK or null if the points are aligned
     */
    private Point2D.Float circumcenter(Point2D.Float pI, Point2D.Float pJ, Point2D.Float pK) {

        // some temporary variables
        final Point2D.Float dIJ = new Point2D.Float(pJ.x - pI.x, pJ.y - pI.y);
        final Point2D.Float dJK = new Point2D.Float(pK.x - pJ.x, pK.y - pJ.y);
        final Point2D.Float dKI = new Point2D.Float(pI.x - pK.x, pI.y - pK.y);
        final float sqI = pI.x * pI.x + pI.y * pI.y;
        final float sqJ = pJ.x * pJ.x + pJ.y * pJ.y;
        final float sqK = pK.x * pK.x + pK.y * pK.y;

        // determinant of the linear system: 0 for aligned points
        final float det = dJK.x * dIJ.y - dIJ.x * dJK.y;
        if (FastMath.abs(det) < 1.0e-10) {
            // points are almost aligned, we cannot compute the circumcenter
            return null;
        }

        // beware, there is a minus sign on Y coordinate!
        return new Point2D.Float((sqI * dJK.y + sqJ * dKI.y + sqK * dIJ.y) / (2 * det),
                -(sqI * dJK.x + sqJ * dKI.x + sqK * dIJ.x) / (2 * det));

    }

    /**
     * Minimize the distance residuals between the points and the circle.
     * <p>
     * We use a non-linear conjugate gradient method with the Polak and
     * Ribiere coefficient for the computation of the search direction. The
     * inner minimization along the search direction is performed using a
     * few Newton steps. It is worthless to spend too much time on this inner
     * minimization, so the convergence threshold can be rather large.
     * </p>
     * 
     * @param maxIter        maximal iterations number on the inner loop (cumulated
     *                       across outer loop iterations)
     * @param innerThreshold inner loop threshold, as a relative difference on
     *                       the cost function value between the two last iterations
     * @param outerThreshold outer loop threshold, as a relative difference on
     *                       the cost function value between the two last iterations
     * @return number of inner loop iterations performed (cumulated
     *         across outer loop iterations)
     * @exception LocalException if we come accross a singularity or if
     *                           we exceed the maximal number of iterations
     */
    public int minimize(int iterMax, float innerThreshold, float outerThreshold) throws LocalException {

        this.computeCost();
        if ((this.J < 1.0e-10) || (FastMath.sqrt(this.dJdx * this.dJdx + this.dJdy * this.dJdy) < 1.0e-10)) {
            // we consider we are already at a local minimum
            return 0;
        }

        float previousJ = this.J;
        float previousU = 0.0f, previousV = 0.0f;
        float previousDJdx = 0.0f, previousDJdy = 0.0f;
        for (int iterations = 0; iterations < iterMax;) {

            // search direction
            float u = -this.dJdx;
            float v = -this.dJdy;
            if (iterations != 0) {
                // Polak-Ribiere coefficient
                final float beta = (this.dJdx * (this.dJdx - previousDJdx) + this.dJdy * (this.dJdy - previousDJdy))
                        / (previousDJdx * previousDJdx + previousDJdy * previousDJdy);
                u += beta * previousU;
                v += beta * previousV;
            }
            previousDJdx = this.dJdx;
            previousDJdy = this.dJdy;
            previousU = u;
            previousV = v;

            // rough minimization along the search direction
            float innerJ;
            do {
                innerJ = this.J;
                final float lambda = this.newtonStep(u, v);
                this.center.x += lambda * u;
                this.center.y += lambda * v;
                this.updateRadius();
                this.computeCost();
            } while ((++iterations < iterMax) && ((FastMath.abs(this.J - innerJ) / this.J) > innerThreshold));

            // global convergence test
            if ((FastMath.abs(this.J - previousJ) / this.J) < outerThreshold) {
                return iterations;
            }
            previousJ = this.J;

        }

        throw new LocalException("unable to converge after " + iterMax + " iterations");

    }

    /**
     * Compute the cost function and its gradient.
     * <p>
     * The results are stored as instance attributes.
     * </p>
     */
    private void computeCost() throws LocalException {
        this.J = 0;
        this.dJdx = 0;
        this.dJdy = 0;
        for (final Float point : this.points) {
            final float dx = point.x - this.center.x;
            final float dy = point.y - this.center.y;
            final float di = (float) FastMath.sqrt(dx * dx + dy * dy);
            if (di < 1.0e-10) {
                throw new LocalException("cost singularity:" + " point at the circle center");
            }
            final float dr = di - this.rHat;
            final float ratio = dr / di;
            this.J += dr * (di + this.rHat);
            this.dJdx += dx * ratio;
            this.dJdy += dy * ratio;
        }
        this.dJdx *= 2.0;
        this.dJdy *= 2.0;
    }

    /**
     * Compute the length of the Newton step in the search direction.
     * 
     * @param u abscissa of the search direction
     * @param v ordinate of the search direction
     * @return value of the step along the search direction
     */
    private float newtonStep(float u, float v) {

        // compute the first and second derivatives of the cost
        // along the specified search direction
        float sum1 = 0, sum2 = 0, sumFac = 0, sumFac2R = 0;
        for (final Float point : this.points) {
            final float dx = this.center.x - point.x;
            final float dy = this.center.y - point.y;
            final float di = (float) FastMath.sqrt(dx * dx + dy * dy);
            final float coeff1 = (dx * u + dy * v) / di;
            final float coeff2 = di - this.rHat;
            sum1 += coeff1 * coeff2;
            sum2 += coeff2 / di;
            sumFac += coeff1;
            sumFac2R += coeff1 * coeff1 / di;
        }

        // step length attempting to nullify the first derivative
        return -sum1 / ((u * u + v * v) * sum2 - sumFac * sumFac / this.points.length + this.rHat * sumFac2R);

    }

    /**
     * Get the circle center.
     * 
     * @return circle center
     */
    public Point2D.Float getCenter() {
        return this.center;
    }

    /**
     * Get the circle radius.
     * 
     * @return circle radius
     */
    public float getRadius() {
        return this.rHat;
    }

    /** Local exception class for algorithm errors. */
    public static class LocalException extends Exception {
        /**
         *
         */
        private static final long serialVersionUID = -208015321222330829L;

        /**
         * Build a new instance with the supplied message.
         * 
         * @param message error message
         */
        public LocalException(String message) {
            super(message);
        }
    }

}
