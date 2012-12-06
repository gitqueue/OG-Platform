/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.calculator.forexblack;

import com.opengamma.analytics.financial.forex.derivative.ForexOptionVanilla;
import com.opengamma.analytics.financial.forex.provider.ForexOptionVanillaBlackFlatMethod;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitorSameMethodAdapter;
import com.opengamma.analytics.financial.provider.description.ForexBlackFlatProviderInterface;
import com.opengamma.util.money.MultipleCurrencyAmount;

/**
 * Calculates the present value of an inflation instruments by discounting for a given MarketBundle
 */
public final class PresentValueForexBlackFlatCalculator extends InstrumentDerivativeVisitorSameMethodAdapter<ForexBlackFlatProviderInterface, MultipleCurrencyAmount> {

  /**
   * The unique instance of the calculator.
   */
  private static final PresentValueForexBlackFlatCalculator INSTANCE = new PresentValueForexBlackFlatCalculator();

  /**
   * Constructor.
   */
  private PresentValueForexBlackFlatCalculator() {
  }

  /**
   * Gets the calculator instance.
   * @return The calculator.
   */
  public static PresentValueForexBlackFlatCalculator getInstance() {
    return INSTANCE;
  }

  /**
   * Pricing methods.
   */
  private static final ForexOptionVanillaBlackFlatMethod METHOD_FX_VAN = ForexOptionVanillaBlackFlatMethod.getInstance();

  @Override
  public MultipleCurrencyAmount visit(final InstrumentDerivative derivative, final ForexBlackFlatProviderInterface blackSmile) {
    return derivative.accept(this, blackSmile);
  }

  // -----     Forex     ------

  @Override
  public MultipleCurrencyAmount visitForexOptionVanilla(final ForexOptionVanilla option, final ForexBlackFlatProviderInterface blackSmile) {
    return METHOD_FX_VAN.presentValue(option, blackSmile);
  }

  @Override
  public MultipleCurrencyAmount visit(final InstrumentDerivative derivative) {
    throw new UnsupportedOperationException();
  }

}
