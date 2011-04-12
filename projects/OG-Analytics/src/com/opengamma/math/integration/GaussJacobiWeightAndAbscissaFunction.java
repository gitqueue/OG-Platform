/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.math.integration;

import org.apache.commons.lang.Validate;
import org.apache.commons.math.util.MathUtils;

import com.opengamma.math.function.DoubleFunction1D;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.function.special.GammaFunction;
import com.opengamma.math.function.special.JacobiPolynomialFunction;
import com.opengamma.math.rootfinding.NewtonRaphsonSingleRootFinder;
import com.opengamma.util.tuple.Pair;

/**
 * Class that generates weights and abscissas for Gauss-Jacobi quadrature. The weights {@latex.inline $w_i$} are given by:
 * {@latex.ilb %preamble{\\usepackage{amsmath}}
 * \\begin{align*}
 * w_i = \\frac{2^{\\alpha + \\beta}(2n + \\alpha + \\beta)\\Gamma(\\alpha + n)\\Gamma(\\beta + n)}{n!\\Gamma(n + \\alpha + \\beta + 1)J_i'(x_i) J_{i - 1}}
 * \\end{align*}
 * }
 * where {@latex.inline $x_i$} is the {@latex.inline $i^{th}$} root of the orthogonal polynomial, {@latex.inline $J_i$} is the {@latex.inline $i^{th}$} polynomial 
 * and {@latex.inline $J_i'$} is the first derivative of the {@latex.inline $i^{th}$} polynomial. The orthogonal polynomial is generated by
 * {@link com.opengamma.math.function.special.JacobiPolynomialFunction}.
 */
public class GaussJacobiWeightAndAbscissaFunction implements QuadratureWeightAndAbscissaFunction {
  private static final JacobiPolynomialFunction JACOBI = new JacobiPolynomialFunction();
  private static final NewtonRaphsonSingleRootFinder ROOT_FINDER = new NewtonRaphsonSingleRootFinder(1e-12);
  private static final Function1D<Double, Double> GAMMA_FUNCTION = new GammaFunction();
  private final double _alpha;
  private final double _beta;
  private final double _c;

  /**
   * Sets {@latex.inline $\\alpha = 0$} and {@latex.inline $\\beta = 0$}
   */
  public GaussJacobiWeightAndAbscissaFunction() {
    this(0, 0);
  }

  /**
   * @param alpha The value of {@latex.inline $\\alpha$} to use when generating the polynomials
   * @param beta The value of {@latex.inline $\\beta$} to use when generating the polynomials
   */
  public GaussJacobiWeightAndAbscissaFunction(final double alpha, final double beta) {
    super();
    _alpha = alpha;
    _beta = beta;
    _c = _alpha + _beta;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GaussianQuadratureData generate(final int n) {
    Validate.isTrue(n > 0, "n > 0");
    final Pair<DoubleFunction1D, DoubleFunction1D>[] polynomials = JACOBI.getPolynomialsAndFirstDerivative(n, _alpha, _beta);
    final Pair<DoubleFunction1D, DoubleFunction1D> pair = polynomials[n];
    final DoubleFunction1D previous = polynomials[n - 1].getFirst();
    final DoubleFunction1D function = pair.getFirst();
    final DoubleFunction1D derivative = pair.getSecond();
    final double[] x = new double[n];
    final double[] w = new double[n];
    double root = 0;
    for (int i = 0; i < n; i++) {
      final double d = 2 * n + _c;
      root = getInitialRootGuess(root, i, n, x);
      root = ROOT_FINDER.getRoot(function, derivative, root);
      x[i] = root;
      w[i] = GAMMA_FUNCTION.evaluate(_alpha + n) * GAMMA_FUNCTION.evaluate(_beta + n) / MathUtils.factorialDouble(n) / GAMMA_FUNCTION.evaluate(n + _c + 1) * d * Math.pow(2, _c)
          / (derivative.evaluate(root) * previous.evaluate(root));
    }
    return new GaussianQuadratureData(x, w);
  }

  private double getInitialRootGuess(final double previousRoot, final int i, final int n, final double[] x) {
    if (i == 0) {
      final double a = _alpha / n;
      final double b = _beta / n;
      final double x1 = (1 + _alpha) * (2.78 / (4 + n * n) + 0.768 * a / n);
      final double x2 = 1 + 1.48 * a + 0.96 * b + 0.452 * a * a + 0.83 * a * b;
      return 1 - x1 / x2;
    }
    if (i == 1) {
      final double x1 = (4.1 + _alpha) / ((1 + _alpha) * (1 + 0.156 * _alpha));
      final double x2 = 1 + 0.06 * (n - 8) * (1 + 0.12 * _alpha) / n;
      final double x3 = 1 + 0.012 * _beta * (1 + 0.25 * Math.abs(_alpha)) / n;
      return previousRoot - (1 - previousRoot) * x1 * x2 * x3;
    }
    if (i == 2) {
      final double x1 = (1.67 + 0.28 * _alpha) / (1 + 0.37 * _alpha);
      final double x2 = 1 + 0.22 * (n - 8) / n;
      final double x3 = 1 + 8 * _beta / ((6.28 + _beta) * n * n);
      return previousRoot - (x[0] - previousRoot) * x1 * x2 * x3;
    }
    if (i == n - 2) {
      final double x1 = (1 + 0.235 * _beta) / (0.766 + 0.119 * _beta);
      final double x2 = 1. / (1 + 0.639 * (n - 4.) / (1 + 0.71 * (n - 4.)));
      final double x3 = 1. / (1 + 20 * _alpha / ((7.5 + _alpha) * n * n));
      return previousRoot + (previousRoot - x[n - 4]) * x1 * x2 * x3;
    }
    if (i == n - 1) {
      final double x1 = (1 + 0.37 * _beta) / (1.67 + 0.28 * _beta);
      final double x2 = 1. / (1 + 0.22 * (n - 8.) / n);
      final double x3 = 1. / (1 + 8. * _alpha / ((6.28 + _alpha) * n * n));
      return previousRoot + (previousRoot - x[n - 3]) * x1 * x2 * x3;
    }
    return 3. * x[i - 1] - 3. * x[i - 2] + x[i - 3];
  }
}
