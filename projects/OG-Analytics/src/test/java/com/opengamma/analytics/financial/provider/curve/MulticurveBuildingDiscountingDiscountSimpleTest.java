/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.curve;

import static org.testng.AssertJUnit.assertEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.time.calendar.Period;
import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.opengamma.analytics.financial.curve.generator.GeneratorCurveYieldInterpolated;
import com.opengamma.analytics.financial.curve.generator.GeneratorYDCurve;
import com.opengamma.analytics.financial.forex.method.FXMatrix;
import com.opengamma.analytics.financial.instrument.InstrumentDefinition;
import com.opengamma.analytics.financial.instrument.cash.CashDefinition;
import com.opengamma.analytics.financial.instrument.fra.ForwardRateAgreementDefinition;
import com.opengamma.analytics.financial.instrument.index.GeneratorDepositIbor;
import com.opengamma.analytics.financial.instrument.index.GeneratorDepositON;
import com.opengamma.analytics.financial.instrument.index.GeneratorInstrument;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedIbor;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedIborMaster;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedON;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedONMaster;
import com.opengamma.analytics.financial.instrument.index.IborIndex;
import com.opengamma.analytics.financial.instrument.index.IndexON;
import com.opengamma.analytics.financial.instrument.swap.SwapFixedIborDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapFixedONDefinition;
import com.opengamma.analytics.financial.interestrate.AbstractInstrumentDerivativeVisitor;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.LastTimeCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.ParSpreadMarketQuoteCurveSensitivityDiscountingCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.ParSpreadMarketQuoteDiscountingCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.PresentValueDiscountingCalculator;
import com.opengamma.analytics.financial.provider.curve.multicurve.MulticurveDiscountBuildingRepository;
import com.opengamma.analytics.financial.provider.description.MulticurveProviderDiscount;
import com.opengamma.analytics.financial.provider.description.MulticurveProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MulticurveSensitivity;
import com.opengamma.analytics.financial.schedule.ScheduleCalculator;
import com.opengamma.analytics.math.interpolation.CombinedInterpolatorExtrapolatorFactory;
import com.opengamma.analytics.math.interpolation.Interpolator1D;
import com.opengamma.analytics.math.interpolation.Interpolator1DFactory;
import com.opengamma.analytics.util.time.TimeCalculator;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;
import com.opengamma.util.timeseries.DoubleTimeSeries;
import com.opengamma.util.timeseries.zoneddatetime.ArrayZonedDateTimeDoubleTimeSeries;
import com.opengamma.util.tuple.Pair;

/**
 * Build of curve in several blocks with relevant Jacobian matrices.
 * @author marc
 */
public class MulticurveBuildingDiscountingDiscountSimpleTest {

  private static final Interpolator1D INTERPOLATOR_LINEAR = CombinedInterpolatorExtrapolatorFactory.getInterpolator(Interpolator1DFactory.LINEAR, Interpolator1DFactory.FLAT_EXTRAPOLATOR,
      Interpolator1DFactory.FLAT_EXTRAPOLATOR);

  private static final LastTimeCalculator MATURITY_CALCULATOR = LastTimeCalculator.getInstance();
  private static final double TOLERANCE_ROOT = 1.0E-10;
  private static final int STEP_MAX = 100;

  private static final Calendar NYC = new MondayToFridayCalendar("NYC");
  private static final Currency USD = Currency.USD;
  private static final FXMatrix FX_MATRIX = new FXMatrix(USD);

  private static final double NOTIONAL = 1.0;

  private static final GeneratorSwapFixedON GENERATOR_OIS_USD = GeneratorSwapFixedONMaster.getInstance().getGenerator("USD1YFEDFUND", NYC);
  private static final IndexON INDEX_ON_USD = GENERATOR_OIS_USD.getIndex();
  private static final GeneratorDepositON GENERATOR_DEPOSIT_ON_USD = new GeneratorDepositON("USD Deposit ON", USD, NYC, INDEX_ON_USD.getDayCount());
  private static final GeneratorSwapFixedIborMaster GENERATOR_SWAP_MASTER = GeneratorSwapFixedIborMaster.getInstance();
  private static final GeneratorSwapFixedIbor USD6MLIBOR3M = GENERATOR_SWAP_MASTER.getGenerator("USD6MLIBOR3M", NYC);
  private static final IborIndex USDLIBOR3M = USD6MLIBOR3M.getIborIndex();
  private static final GeneratorDepositIbor GENERATOR_USDLIBOR3M = new GeneratorDepositIbor("GENERATOR_USDLIBOR3M", USDLIBOR3M);

  private static final ZonedDateTime NOW = DateUtils.getUTCDate(2011, 9, 28);

  private static final ArrayZonedDateTimeDoubleTimeSeries TS_EMPTY = new ArrayZonedDateTimeDoubleTimeSeries();
  private static final ArrayZonedDateTimeDoubleTimeSeries TS_ON_USD_WITH_TODAY = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtils.getUTCDate(2011, 9, 27),
      DateUtils.getUTCDate(2011, 9, 28)}, new double[] {0.07, 0.08});
  private static final ArrayZonedDateTimeDoubleTimeSeries TS_ON_USD_WITHOUT_TODAY = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtils.getUTCDate(2011, 9, 27),
      DateUtils.getUTCDate(2011, 9, 28)}, new double[] {0.07, 0.08});
  @SuppressWarnings("rawtypes")
  private static final DoubleTimeSeries[] TS_FIXED_OIS_USD_WITH_TODAY = new DoubleTimeSeries[] {TS_EMPTY, TS_ON_USD_WITH_TODAY};
  @SuppressWarnings("rawtypes")
  private static final DoubleTimeSeries[] TS_FIXED_OIS_USD_WITHOUT_TODAY = new DoubleTimeSeries[] {TS_EMPTY, TS_ON_USD_WITHOUT_TODAY};

  private static final ArrayZonedDateTimeDoubleTimeSeries TS_IBOR_USD3M_WITH_TODAY = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtils.getUTCDate(2011, 9, 27),
      DateUtils.getUTCDate(2011, 9, 28)}, new double[] {0.0035, 0.0036});
  private static final ArrayZonedDateTimeDoubleTimeSeries TS_IBOR_USD3M_WITHOUT_TODAY = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtils.getUTCDate(2011, 9, 27)},
      new double[] {0.0035});

  @SuppressWarnings("rawtypes")
  private static final DoubleTimeSeries[] TS_FIXED_IBOR_USD3M_WITH_TODAY = new DoubleTimeSeries[] {TS_IBOR_USD3M_WITH_TODAY};
  @SuppressWarnings("rawtypes")
  private static final DoubleTimeSeries[] TS_FIXED_IBOR_USD3M_WITHOUT_TODAY = new DoubleTimeSeries[] {TS_IBOR_USD3M_WITHOUT_TODAY};

  private static final String CURVE_NAME_DSC_USD = "USD Dsc";
  private static final String CURVE_NAME_FWD3_USD = "USD Fwd 3M";

  /** Market values for the dsc USD curve */
  public static final double[] DSC_USD_MARKET_QUOTES = new double[] {0.0400, 0.0400, 0.0400, 0.0400, 0.0400, 0.0400, 0.0400, 0.0400, 0.0400, 0.0400, 0.0400, 0.0400};
  /** Generators for the dsc USD curve */
  public static final GeneratorInstrument[] DSC_USD_GENERATORS = new GeneratorInstrument[] {GENERATOR_DEPOSIT_ON_USD, GENERATOR_OIS_USD, GENERATOR_OIS_USD, GENERATOR_OIS_USD, GENERATOR_OIS_USD,
      GENERATOR_OIS_USD, GENERATOR_OIS_USD, GENERATOR_OIS_USD, GENERATOR_OIS_USD, GENERATOR_OIS_USD, GENERATOR_OIS_USD, GENERATOR_OIS_USD};
  /** Tenors for the dsc USD curve */
  public static final Period[] DSC_USD_TENOR = new Period[] {Period.ofDays(0), Period.ofMonths(1), Period.ofMonths(2), Period.ofMonths(3), Period.ofMonths(6), Period.ofMonths(9), Period.ofYears(1),
      Period.ofYears(2), Period.ofYears(3), Period.ofYears(4), Period.ofYears(5), Period.ofYears(10)};

  /** Market values for the Fwd 3M USD curve */
  public static final double[] FWD3_USD_MARKET_QUOTES = new double[] {0.0420, 0.0420, 0.0420, 0.0430, 0.0470, 0.0540, 0.0570, 0.0600};
  /** Generators for the Fwd 3M USD curve */
  public static final GeneratorInstrument[] FWD3_USD_GENERATORS = new GeneratorInstrument[] {GENERATOR_USDLIBOR3M, USD6MLIBOR3M, USD6MLIBOR3M, USD6MLIBOR3M, USD6MLIBOR3M, USD6MLIBOR3M, USD6MLIBOR3M,
      USD6MLIBOR3M};
  /** Tenors for the Fwd 3M USD curve */
  public static final Period[] FWD3_USD_TENOR = new Period[] {Period.ofMonths(0), Period.ofMonths(6), Period.ofYears(1), Period.ofYears(2), Period.ofYears(3), Period.ofYears(5), Period.ofYears(7),
      Period.ofYears(10)};

  /** Standard USD discounting curve instrument definitions */
  public static final InstrumentDefinition<?>[] DEFINITIONS_DSC_USD;
  /** Standard USD Forward 3M curve instrument definitions */
  public static final InstrumentDefinition<?>[] DEFINITIONS_FWD3_USD;

  /** Units of curves */
  public static final int[] NB_UNITS = new int[] {2};
  public static final int NB_BLOCKS = NB_UNITS.length;
  public static final InstrumentDefinition<?>[][][][] DEFINITIONS_UNITS = new InstrumentDefinition<?>[NB_BLOCKS][][][];
  public static final GeneratorYDCurve[][][] GENERATORS_UNITS = new GeneratorYDCurve[NB_BLOCKS][][];
  public static final String[][][] NAMES_UNITS = new String[NB_BLOCKS][][];
  public static final MulticurveProviderDiscount KNOWN_DATA = new MulticurveProviderDiscount(FX_MATRIX);
  public static final LinkedHashMap<String, Currency> DSC_MAP = new LinkedHashMap<String, Currency>();
  public static final LinkedHashMap<String, IndexON[]> FWD_ON_MAP = new LinkedHashMap<String, IndexON[]>();
  public static final LinkedHashMap<String, IborIndex[]> FWD_IBOR_MAP = new LinkedHashMap<String, IborIndex[]>();

  static {
    DEFINITIONS_DSC_USD = getDefinitions(DSC_USD_MARKET_QUOTES, DSC_USD_GENERATORS, DSC_USD_TENOR, new Double[DSC_USD_MARKET_QUOTES.length]);
    DEFINITIONS_FWD3_USD = getDefinitions(FWD3_USD_MARKET_QUOTES, FWD3_USD_GENERATORS, FWD3_USD_TENOR, new Double[FWD3_USD_MARKET_QUOTES.length]);
    for (int loopblock = 0; loopblock < NB_BLOCKS; loopblock++) {
      DEFINITIONS_UNITS[loopblock] = new InstrumentDefinition<?>[NB_UNITS[loopblock]][][];
      GENERATORS_UNITS[loopblock] = new GeneratorYDCurve[NB_UNITS[loopblock]][];
      NAMES_UNITS[loopblock] = new String[NB_UNITS[loopblock]][];
    }
    DEFINITIONS_UNITS[0][0] = new InstrumentDefinition<?>[][] {DEFINITIONS_DSC_USD};
    DEFINITIONS_UNITS[0][1] = new InstrumentDefinition<?>[][] {DEFINITIONS_FWD3_USD};
    GeneratorYDCurve genIntLin = new GeneratorCurveYieldInterpolated(MATURITY_CALCULATOR, INTERPOLATOR_LINEAR);
    GENERATORS_UNITS[0][0] = new GeneratorYDCurve[] {genIntLin};
    GENERATORS_UNITS[0][1] = new GeneratorYDCurve[] {genIntLin};
    NAMES_UNITS[0][0] = new String[] {CURVE_NAME_DSC_USD};
    NAMES_UNITS[0][1] = new String[] {CURVE_NAME_FWD3_USD};
    DSC_MAP.put(CURVE_NAME_DSC_USD, USD);
    FWD_ON_MAP.put(CURVE_NAME_DSC_USD, new IndexON[] {INDEX_ON_USD});
    FWD_IBOR_MAP.put(CURVE_NAME_FWD3_USD, new IborIndex[] {USDLIBOR3M});
  }

  public static final String NOT_USED = "Not used";
  public static final String[] NOT_USED_2 = {NOT_USED, NOT_USED};

  public static InstrumentDefinition<?>[] getDefinitions(final double[] marketQuotes, final GeneratorInstrument[] generators, final Period[] tenors, final Double[] other) {
    final InstrumentDefinition<?>[] definitions = new InstrumentDefinition<?>[marketQuotes.length];
    for (int loopmv = 0; loopmv < marketQuotes.length; loopmv++) {
      definitions[loopmv] = generators[loopmv].generateInstrument(NOW, tenors[loopmv], marketQuotes[loopmv], NOTIONAL, other[loopmv]);
    }
    return definitions;
  }

  private static List<Pair<MulticurveProviderDiscount, CurveBuildingBlockBundle>> CURVES_PAR_SPREAD_MQ_WITHOUT_TODAY_BLOCK = new ArrayList<Pair<MulticurveProviderDiscount, CurveBuildingBlockBundle>>();

  // Calculator
  private static final PresentValueDiscountingCalculator PVC = PresentValueDiscountingCalculator.getInstance();
  private static final ParSpreadMarketQuoteDiscountingCalculator PSMQC = ParSpreadMarketQuoteDiscountingCalculator.getInstance();
  private static final ParSpreadMarketQuoteCurveSensitivityDiscountingCalculator PSMQCSC = ParSpreadMarketQuoteCurveSensitivityDiscountingCalculator.getInstance();

  private static final MulticurveDiscountBuildingRepository CURVE_BUILDING_REPOSITORY = new MulticurveDiscountBuildingRepository(TOLERANCE_ROOT, TOLERANCE_ROOT, STEP_MAX);

  private static final double TOLERANCE_CAL = 1.0E-9;

  @BeforeSuite
  static void initClass() {
    for (int loopblock = 0; loopblock < NB_BLOCKS; loopblock++) {
      CURVES_PAR_SPREAD_MQ_WITHOUT_TODAY_BLOCK.add(makeCurvesFromDefinitions(DEFINITIONS_UNITS[loopblock], GENERATORS_UNITS[loopblock], NAMES_UNITS[loopblock], KNOWN_DATA, PSMQC, PSMQCSC, false));
    }
  }

  @Test
  public void curveConstructionGeneratorOtherBlocks() {
    for (int loopblock = 0; loopblock < NB_BLOCKS; loopblock++) {
      curveConstructionTest(DEFINITIONS_UNITS[loopblock], CURVES_PAR_SPREAD_MQ_WITHOUT_TODAY_BLOCK.get(loopblock).getFirst(), false, loopblock);
    }
    int t = 0;
    t++;
  }

  public void curveConstructionTest(final InstrumentDefinition<?>[][][] definitions, final MulticurveProviderDiscount curves, final boolean withToday, int block) {
    int nbBlocks = definitions.length;
    for (int loopblock = 0; loopblock < nbBlocks; loopblock++) {
      InstrumentDerivative[][] instruments = convert(definitions[loopblock], loopblock, withToday);
      double[][] pv = new double[instruments.length][];
      for (int loopcurve = 0; loopcurve < instruments.length; loopcurve++) {
        pv[loopcurve] = new double[instruments[loopcurve].length];
        for (int loopins = 0; loopins < instruments[loopcurve].length; loopins++) {
          pv[loopcurve][loopins] = curves.getFxRates().convert(PVC.visit(instruments[loopcurve][loopins], curves), USD).getAmount();
          assertEquals("Curve construction: block " + block + ", unit " + loopblock + " - instrument " + loopins, 0, pv[loopcurve][loopins], TOLERANCE_CAL);
        }
      }
    }
  }

  @Test(enabled = false)
  /**
   * Analyzes the shape of the forward curve.
   */
  public void forwardAnalysis() {
    MulticurveProviderInterface marketDsc = CURVES_PAR_SPREAD_MQ_WITHOUT_TODAY_BLOCK.get(0).getFirst();
    int jump = 1;
    int startIndex = 0;
    int nbDate = 2750;
    ZonedDateTime startDate = ScheduleCalculator.getAdjustedDate(NOW, USDLIBOR3M.getSpotLag() + startIndex * jump, NYC);
    double[] rateDsc = new double[nbDate];
    double[] startTime = new double[nbDate];
    try {
      FileWriter writer = new FileWriter("fwd-dsc.csv");
      for (int loopdate = 0; loopdate < nbDate; loopdate++) {
        startTime[loopdate] = TimeCalculator.getTimeBetween(NOW, startDate);
        ZonedDateTime endDate = ScheduleCalculator.getAdjustedDate(startDate, USDLIBOR3M);
        double endTime = TimeCalculator.getTimeBetween(NOW, endDate);
        double accrualFactor = USDLIBOR3M.getDayCount().getDayCountFraction(startDate, endDate);
        rateDsc[loopdate] = marketDsc.getForwardRate(USDLIBOR3M, startTime[loopdate], endTime, accrualFactor);
        startDate = ScheduleCalculator.getAdjustedDate(startDate, jump, NYC);
        writer.append(0.0 + "," + startTime[loopdate] + "," + rateDsc[loopdate] + "\n");
      }
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Pair<MulticurveProviderDiscount, CurveBuildingBlockBundle> makeCurvesFromDefinitions(final InstrumentDefinition<?>[][][] definitions, GeneratorYDCurve[][] curveGenerators,
      String[][] curveNames, MulticurveProviderDiscount knownData, final AbstractInstrumentDerivativeVisitor<MulticurveProviderInterface, Double> calculator,
      final AbstractInstrumentDerivativeVisitor<MulticurveProviderInterface, MulticurveSensitivity> sensitivityCalculator, boolean withToday) {
    int nbUnits = curveGenerators.length;
    double[][] parametersGuess = new double[nbUnits][];
    GeneratorYDCurve[][] generatorFinal = new GeneratorYDCurve[nbUnits][];
    InstrumentDerivative[][][] instruments = new InstrumentDerivative[nbUnits][][];
    for (int loopunit = 0; loopunit < nbUnits; loopunit++) {
      generatorFinal[loopunit] = new GeneratorYDCurve[curveGenerators[loopunit].length];
      int nbInsUnit = 0;
      for (int loopcurve = 0; loopcurve < curveGenerators[loopunit].length; loopcurve++) {
        nbInsUnit += definitions[loopunit][loopcurve].length;
      }
      parametersGuess[loopunit] = new double[nbInsUnit];
      int startCurve = 0; // First parameter index of the curve in the unit. 
      instruments[loopunit] = convert(definitions[loopunit], loopunit, withToday);
      for (int loopcurve = 0; loopcurve < curveGenerators[loopunit].length; loopcurve++) {
        generatorFinal[loopunit][loopcurve] = curveGenerators[loopunit][loopcurve].finalGenerator(instruments[loopunit][loopcurve]);
        double[] guessCurve = generatorFinal[loopunit][loopcurve].initialGuess(initialGuess(definitions[loopunit][loopcurve]));
        System.arraycopy(guessCurve, 0, parametersGuess[loopunit], startCurve, instruments[loopunit][loopcurve].length);
        startCurve += instruments[loopunit][loopcurve].length;
      }
    }
    return CURVE_BUILDING_REPOSITORY.makeCurvesFromDerivatives(instruments, generatorFinal, curveNames, parametersGuess, knownData, DSC_MAP, FWD_IBOR_MAP, FWD_ON_MAP, calculator,
        sensitivityCalculator);
  }

  @SuppressWarnings("unchecked")
  private static InstrumentDerivative[][] convert(InstrumentDefinition<?>[][] definitions, int unit, boolean withToday) {
    int nbDef = 0;
    for (int loopdef1 = 0; loopdef1 < definitions.length; loopdef1++) {
      nbDef += definitions[loopdef1].length;
    }
    final InstrumentDerivative[][] instruments = new InstrumentDerivative[definitions.length][];
    for (int loopcurve = 0; loopcurve < definitions.length; loopcurve++) {
      instruments[loopcurve] = new InstrumentDerivative[definitions[loopcurve].length];
      int loopins = 0;
      for (final InstrumentDefinition<?> instrument : definitions[loopcurve]) {
        InstrumentDerivative ird;
        if (instrument instanceof SwapFixedONDefinition) {
          ird = ((SwapFixedONDefinition) instrument).toDerivative(NOW, getTSSwapFixedON(withToday, unit), NOT_USED_2);
        } else {
          if (instrument instanceof SwapFixedIborDefinition) {
            ird = ((SwapFixedIborDefinition) instrument).toDerivative(NOW, getTSSwapFixedIbor(withToday, unit), NOT_USED_2);
          } else {
            ird = instrument.toDerivative(NOW, NOT_USED_2);
          }
        }
        instruments[loopcurve][loopins++] = ird;
      }
    }
    return instruments;
  }

  @SuppressWarnings("rawtypes")
  private static DoubleTimeSeries[] getTSSwapFixedON(Boolean withToday, Integer unit) {
    switch (unit) {
      case 0:
        return withToday ? TS_FIXED_OIS_USD_WITH_TODAY : TS_FIXED_OIS_USD_WITHOUT_TODAY;
      default:
        throw new IllegalArgumentException(unit.toString());
    }
  }

  @SuppressWarnings("rawtypes")
  private static DoubleTimeSeries[] getTSSwapFixedIbor(Boolean withToday, Integer unit) {
    switch (unit) {
      case 0:
        return withToday ? TS_FIXED_IBOR_USD3M_WITH_TODAY : TS_FIXED_IBOR_USD3M_WITHOUT_TODAY;
      case 1:
        return withToday ? TS_FIXED_IBOR_USD3M_WITH_TODAY : TS_FIXED_IBOR_USD3M_WITHOUT_TODAY;
      default:
        throw new IllegalArgumentException(unit.toString());
    }
  }

  private static double[] initialGuess(InstrumentDefinition<?>[] definitions) {
    double[] result = new double[definitions.length];
    int loopr = 0;
    for (int loopcurve = 0; loopcurve < definitions.length; loopcurve++) {
      result[loopr++] = initialGuess(definitions[loopcurve]);
    }
    return result;
  }

  private static double initialGuess(InstrumentDefinition<?> instrument) {
    if (instrument instanceof SwapFixedONDefinition) {
      return ((SwapFixedONDefinition) instrument).getFixedLeg().getNthPayment(0).getRate();
    }
    if (instrument instanceof SwapFixedIborDefinition) {
      return ((SwapFixedIborDefinition) instrument).getFixedLeg().getNthPayment(0).getRate();
    }
    if (instrument instanceof ForwardRateAgreementDefinition) {
      return ((ForwardRateAgreementDefinition) instrument).getRate();
    }
    if (instrument instanceof CashDefinition) {
      return ((CashDefinition) instrument).getRate();
    }
    return 0.01;
  }

}
