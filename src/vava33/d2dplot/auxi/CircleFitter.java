package vava33.d2dplot.auxi;

import java.awt.geom.Point2D;

import org.apache.commons.math3.util.FastMath;

public class CircleFitter {

    /** Current circle center. */
    private Point2D.Float center;

    /** Current circle radius. */
    private float rHat;

    /** Circular ring sample points. */
    private Point2D.Float[] points;

    /** Current cost function value. */
    private float J;

    /** Current cost function gradient. */
    private float dJdx;
    private float dJdy;
    
    /** Build a new instance with a default current circle.
     */
    public CircleFitter() {
      center = new Point2D.Float(0.0f, 0.0f);
      rHat   = 1.0f;
      points = null;
    }


    /** Initialize an approximate circle based on all triplets.
     * @param points circular ring sample points
     * @exception LocalException if all points are aligned
     */
    public void initialize(Point2D.Float[] points)
      throws LocalException {

      // store the points array
      this.points = points;

      // analyze all possible points triplets
      center.x = 0.0f;
      center.y = 0.0f;
      int n = 0;
      for (int i = 0; i < (points.length - 2); ++i) {
        Point2D.Float p1 = (Point2D.Float) points[i];
        for (int j = i + 1; j < (points.length - 1); ++j) {
          Point2D.Float p2 = (Point2D.Float) points[j];
          for (int k = j + 1; k < points.length; ++k) {
            Point2D.Float p3 = (Point2D.Float) points[k];

            // compute the triangle circumcenter
            Point2D.Float cc = circumcenter(p1, p2, p3);
            if (cc != null) {
              // the points are not aligned, we have a circumcenter
              ++n;
              center.x += cc.x;
              center.y += cc.y;
            }
          }
        }
      }

      if (n == 0) {
        throw new LocalException("all points are aligned");
      }

      // initialize using the circumcenters average
      center.x /= n;
      center.y /= n;
      updateRadius();

    }

    /** Update the circle radius.
     */
    private void updateRadius() {
      rHat = 0;
      for (int i = 0; i < points.length; ++i) {
        float dx = points[i].x - center.x;
        float dy = points[i].y - center.y;
        rHat += FastMath.sqrt(dx * dx + dy * dy);
      }
      rHat /= points.length;
    }

    /** Compute the circumcenter of three points.
     * @param pI first point
     * @param pJ second point
     * @param pK third point
     * @return circumcenter of pI, pJ and pK or null if the points are aligned
     */
    private Point2D.Float circumcenter(Point2D.Float pI,
                                        Point2D.Float pJ,
                                        Point2D.Float pK) {

      // some temporary variables
      Point2D.Float  dIJ = new Point2D.Float(pJ.x - pI.x, pJ.y - pI.y);
      Point2D.Float  dJK = new Point2D.Float(pK.x - pJ.x, pK.y - pJ.y);
      Point2D.Float  dKI = new Point2D.Float(pI.x - pK.x, pI.y - pK.y);
      float sqI = pI.x * pI.x + pI.y * pI.y;
      float sqJ = pJ.x * pJ.x + pJ.y * pJ.y;
      float sqK = pK.x * pK.x + pK.y * pK.y;

      // determinant of the linear system: 0 for aligned points
      float det = dJK.x * dIJ.y - dIJ.x * dJK.y;
      if (FastMath.abs(det) < 1.0e-10) {
        // points are almost aligned, we cannot compute the circumcenter
        return null;
      }

      // beware, there is a minus sign on Y coordinate!
      return new Point2D.Float(
             (sqI * dJK.y + sqJ * dKI.y + sqK * dIJ.y) / (2 * det),
            -(sqI * dJK.x + sqJ * dKI.x + sqK * dIJ.x) / (2 * det));

    }

    /** Minimize the distance residuals between the points and the circle.
     * <p>We use a non-linear conjugate gradient method with the Polak and
     * Ribiere coefficient for the computation of the search direction. The
     * inner minimization along the search direction is performed using a
     * few Newton steps. It is worthless to spend too much time on this inner
     * minimization, so the convergence threshold can be rather large.</p>
     * @param maxIter maximal iterations number on the inner loop (cumulated
     * across outer loop iterations)
     * @param innerThreshold inner loop threshold, as a relative difference on
     * the cost function value between the two last iterations
     * @param outerThreshold outer loop threshold, as a relative difference on
     * the cost function value between the two last iterations
     * @return number of inner loop iterations performed (cumulated
     * across outer loop iterations)
     * @exception LocalException if we come accross a singularity or if
     * we exceed the maximal number of iterations
     */
    public int minimize(int iterMax,
                        float innerThreshold, float outerThreshold)
      throws LocalException {

      computeCost();
      if ((J < 1.0e-10) || (FastMath.sqrt(dJdx * dJdx + dJdy * dJdy) < 1.0e-10)) {
        // we consider we are already at a local minimum
        return 0;
      }

      float previousJ = J;
      float previousU = 0.0f, previousV = 0.0f;
      float previousDJdx = 0.0f, previousDJdy = 0.0f;
      for (int iterations = 0; iterations < iterMax;) {

        // search direction
        float u = -dJdx;
        float v = -dJdy;
        if (iterations != 0) {
          // Polak-Ribiere coefficient
          float beta =
            (dJdx * (dJdx - previousDJdx) + dJdy * (dJdy - previousDJdy))
          / (previousDJdx * previousDJdx + previousDJdy * previousDJdy);
          u += beta * previousU;
          v += beta * previousV;
        }
        previousDJdx = dJdx;
        previousDJdy = dJdy;
        previousU    = u;
        previousV    = v;

        // rough minimization along the search direction
        float innerJ;
        do {
          innerJ = J;
          float lambda = newtonStep(u, v);
          center.x += lambda * u;
          center.y += lambda * v;
          updateRadius();
          computeCost();
        } while ((++iterations < iterMax)
                 && ((FastMath.abs(J - innerJ) / J) > innerThreshold));

        // global convergence test
        if ((FastMath.abs(J - previousJ) / J) < outerThreshold) {
          return iterations;
        }
        previousJ = J;

      }

      throw new LocalException("unable to converge after "
                               + iterMax + " iterations");

    }

    /** Compute the cost function and its gradient.
     * <p>The results are stored as instance attributes.</p>
     */
    private void computeCost() throws LocalException {
      J    = 0;
      dJdx = 0;
      dJdy = 0;
      for (int i = 0; i < points.length; ++i) {
        float dx = points[i].x - center.x;
        float dy = points[i].y - center.y;
        float di = (float) FastMath.sqrt(dx * dx + dy * dy);
        if (di < 1.0e-10) {
          throw new LocalException("cost singularity:"
                                   + " point at the circle center");
        }
        float dr    = di - rHat;
        float ratio = dr / di;
        J    += dr * (di + rHat);
        dJdx += dx * ratio;
        dJdy += dy * ratio;
      }
      dJdx *= 2.0;
      dJdy *= 2.0;
    }

    /** Compute the length of the Newton step in the search direction.
     * @param u abscissa of the search direction
     * @param v ordinate of the search direction
     * @return value of the step along the search direction
     */
    private float newtonStep(float u, float v) {

      // compute the first and second derivatives of the cost
      // along the specified search direction
      float sum1 = 0, sum2 = 0, sumFac = 0, sumFac2R = 0;
      for (int i = 0; i < points.length; ++i) {
        float dx     = center.x - points[i].x;
        float dy     = center.y - points[i].y;
        float di     = (float) FastMath.sqrt(dx * dx + dy * dy);
        float coeff1 = (dx * u + dy * v) /  di;
        float coeff2 = di - rHat;
        sum1         += coeff1 * coeff2;
        sum2         += coeff2 / di;
        sumFac       += coeff1;
        sumFac2R     += coeff1 * coeff1 / di;
      }

      // step length attempting to nullify the first derivative
      return -sum1 / ((u * u + v * v) * sum2
                      - sumFac * sumFac / points.length
                      + rHat * sumFac2R);

    }

    /** Get the circle center.
     * @return circle center
     */
    public Point2D.Float getCenter() {
      return center;
    }

    /** Get the circle radius.
     * @return circle radius
     */
    public float getRadius() {
      return rHat;
    }

    /** Local exception class for algorithm errors. */
    public static class LocalException extends Exception {
      /** Build a new instance with the supplied message.
       * @param message error message
       */
      public LocalException(String message) {
        super(message);
      }
    }

}
