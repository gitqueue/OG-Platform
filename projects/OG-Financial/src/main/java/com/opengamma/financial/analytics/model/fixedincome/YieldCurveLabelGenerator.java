/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.fixedincome;

import java.util.Set;

import com.opengamma.financial.analytics.curve.CurveSpecification;
import com.opengamma.financial.analytics.ircurve.FixedIncomeStripWithSecurity;
import com.opengamma.financial.analytics.ircurve.InterpolatedYieldCurveSpecificationWithSecurities;
import com.opengamma.financial.analytics.ircurve.StripInstrumentType;
import com.opengamma.financial.analytics.ircurve.strips.CurveNodeWithIdentifier;
import com.opengamma.financial.analytics.ircurve.strips.RateFutureNode;

/**
 * 
 */
public class YieldCurveLabelGenerator {

  public static Object[] getLabels(final InterpolatedYieldCurveSpecificationWithSecurities spec) {
    final Set<FixedIncomeStripWithSecurity> strips = spec.getStrips();
    final int n = strips.size();
    final Object[] labels = new Object[n];
    int i = 0;
    for (final FixedIncomeStripWithSecurity strip : strips) {
      labels[i++] = strip.getSecurityIdentifier().getExternalId();
    }
    return labels;
  }

  public static Object[] getHybridLabels(final InterpolatedYieldCurveSpecificationWithSecurities spec) {
    final Set<FixedIncomeStripWithSecurity> strips = spec.getStrips();
    final int n = strips.size();
    final Object[] labels = new Object[n];
    int i = 0;
    for (final FixedIncomeStripWithSecurity strip : strips) {
      if (strip.getInstrumentType().equals(StripInstrumentType.FUTURE)) {
        labels[i++] = strip.getSecurityIdentifier().getExternalId().getValue();
      } else {
        labels[i++] = strip.getTenor().getPeriod().toString().substring(1);
      }
    }
    return labels;
  }

  public static Object[] getHybridLabels(final CurveSpecification spec) {
    final Set<CurveNodeWithIdentifier> nodes = spec.getNodes();
    final int n = nodes.size();
    final Object[] labels = new Object[n];
    int i = 0;
    for (final CurveNodeWithIdentifier node : nodes) {
      if (node.getCurveNode() instanceof RateFutureNode) {
        labels[i++] = node.getIdentifier().getValue();
      } else {
        labels[i++] = node.getCurveNode().getResolvedMaturity().getPeriod().toString().substring(1);
      }
    }
    return labels;
  }
}
