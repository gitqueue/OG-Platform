package com.opengamma.analytics.financial.credit.isdastandardmodel;

import static com.opengamma.financial.convention.businessday.BusinessDayDateUtils.addWorkDays;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;
import org.threeten.bp.LocalDate;

import com.opengamma.analytics.financial.credit.isdastandardmodel.CDSAnalytic;
import com.opengamma.analytics.financial.credit.isdastandardmodel.CDSQuoteConvention;
import com.opengamma.analytics.financial.credit.isdastandardmodel.ISDACompliantYieldCurve;
import com.opengamma.analytics.financial.credit.isdastandardmodel.MarketQuoteConverter;
import com.opengamma.analytics.financial.credit.isdastandardmodel.PointsUpFront;
import com.opengamma.analytics.financial.credit.isdastandardmodel.QuotedSpread;
import com.opengamma.analytics.financial.model.BumpType;
import com.opengamma.util.test.TestGroup;

/**
 * Test.
 */
@Test(groups = TestGroup.UNIT)
public class CS01FromQuotedSpreadsTest extends ISDABaseTest {

  private static final MarketQuoteConverter PUF_CONVERTER = new MarketQuoteConverter();
  protected static final double NOTIONAL = 1e6;
  private static final LocalDate TRADE_DATE = LocalDate.of(2013, 6, 4); //today
  private static final LocalDate EFFECTIVE_DATE = TRADE_DATE.plusDays(1); // AKA stepin date
  private static final LocalDate CASH_SETTLE_DATE = addWorkDays(TRADE_DATE, 3, DEFAULT_CALENDAR); // AKA valuation date
  private static final LocalDate STARTDATE = LocalDate.of(2013, 3, 20);
  private static final LocalDate[] MATURITIES = new LocalDate[] {LocalDate.of(2013, 6, 20), LocalDate.of(2013, 9, 20), LocalDate.of(2013, 12, 20), LocalDate.of(2014, 3, 20),
    LocalDate.of(2014, 6, 20), LocalDate.of(2014, 9, 20), LocalDate.of(2014, 12, 20), LocalDate.of(2015, 3, 20), LocalDate.of(2015, 6, 20), LocalDate.of(2015, 9, 20), LocalDate.of(2015, 12, 20),
    LocalDate.of(2016, 3, 20), LocalDate.of(2016, 6, 20), LocalDate.of(2016, 9, 20), LocalDate.of(2016, 12, 20), LocalDate.of(2017, 3, 20), LocalDate.of(2017, 6, 20), LocalDate.of(2017, 9, 20),
    LocalDate.of(2017, 12, 20), LocalDate.of(2018, 3, 20), LocalDate.of(2018, 6, 20), LocalDate.of(2018, 9, 20), LocalDate.of(2018, 12, 20), LocalDate.of(2019, 3, 20), LocalDate.of(2019, 6, 20),
    LocalDate.of(2019, 9, 20), LocalDate.of(2019, 12, 20), LocalDate.of(2020, 3, 20), LocalDate.of(2020, 6, 20), LocalDate.of(2020, 9, 20), LocalDate.of(2020, 12, 20), LocalDate.of(2021, 3, 20),
    LocalDate.of(2021, 6, 20), LocalDate.of(2021, 9, 20), LocalDate.of(2021, 12, 20), LocalDate.of(2022, 3, 20), LocalDate.of(2022, 6, 20), LocalDate.of(2022, 9, 20), LocalDate.of(2022, 12, 20),
    LocalDate.of(2023, 3, 20), LocalDate.of(2023, 6, 20) };

  private static final double[] QUOTED_SPREADS = new double[] {8.97, 9.77, 10.7, 11.96, 13.17, 15.59, 17.8, 19.66, 21.35, 23.91, 26.54, 28.56, 30.63, 32.41, 34.08, 35.33, 36.74, 38.9, 40.88, 42.71,
    44.49, 46.92, 49.2, 51.36, 53.5, 55.58, 57.59, 59.49, 61.4, 62.76, 64.11, 65.35, 66.55, 67.58, 68.81, 69.81, 70.79, 71.65, 72.58, 73.58, 74.2 };

  private static final LocalDate[] BUCKET_DATES = new LocalDate[] {LocalDate.of(2013, 12, 20), LocalDate.of(2014, 6, 20), LocalDate.of(2015, 6, 20), LocalDate.of(2016, 6, 20),
    LocalDate.of(2017, 6, 20), LocalDate.of(2018, 6, 20), LocalDate.of(2019, 6, 20), LocalDate.of(2020, 6, 20), LocalDate.of(2021, 6, 20), LocalDate.of(2022, 6, 20), LocalDate.of(2023, 6, 20),
    LocalDate.of(2028, 6, 20), LocalDate.of(2033, 6, 20), LocalDate.of(2043, 6, 20) };

  private static final double[] QUOTED_SPREADS_AT_BUCKET_DATES = new double[] {10.7, 13.17, 21.35, 30.63, 36.74, 44.49, 53.5, 61.4, 66.55, 70.79, 74.2, 74.2, 74.2, 74.2 };

  private static final double COUPON = 100;

  //yield curve
  private static final LocalDate SPOT_DATE = LocalDate.of(2013, 6, 6);
  private static final String[] YIELD_CURVE_POINTS = new String[] {"1M", "2M", "3M", "6M", "1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "12Y", "15Y", "20Y", "25Y", "30Y" };
  private static final String[] YIELD_CURVE_INSTRUMENTS = new String[] {"M", "M", "M", "M", "M", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S" };
  private static final double[] YIELD_CURVE_RATES = new double[] {0.00194, 0.002292, 0.002733, 0.004153, 0.006902, 0.004575, 0.006585, 0.00929, 0.012175, 0.0149, 0.01745, 0.019595, 0.02144, 0.023045,
    0.02567, 0.02825, 0.03041, 0.031425, 0.03202 };
  private static final ISDACompliantYieldCurve YIELD_CURVE = makeYieldCurve(TRADE_DATE, SPOT_DATE, YIELD_CURVE_POINTS, YIELD_CURVE_INSTRUMENTS, YIELD_CURVE_RATES);

  //BBG numbers 
  private static final double[] CASH_PAYMENTS = new double[] {-2543, -4843, -7065, -9182, -11284, -13161, -14922, -16610, -18279, -19651, -20847, -22077, -23201, -24307, -25336, -26417, -27396,
    -27969, -28493, -28968, -29401, -29395, -29335, -29217, -29027, -28767, -28439, -28087, -27647, -27501, -27287, -27083, -26866, -26725, -26370, -26140, -25895, -25707, -25414, -25014, -24925 };

  // These numbers come from The ISDA excel plugin
  private static final double[] PARELLEL_CS01 = new double[] {4.44388460893843, 30.033640328983, 55.3853989749605, 80.4665679983788, 106.113611507615, 131.76855171026, 157.157114109902,
    182.279368810202, 207.956446565041, 233.488342547238, 258.618042600828, 283.695469500717, 308.916298196751, 334.008749654446, 358.675305840658, 382.994684077393, 407.64905916453,
    431.852151546102, 455.57043360692, 478.808476009465, 502.318585908348, 525.220273167086, 547.569648624322, 569.368808982763, 591.319501611551, 612.909785294567, 633.906026965003,
    654.590133161596, 675.110290122106, 695.67623150317, 715.659552150048, 735.161267994736, 754.81254765758, 774.280598999456, 792.972762570697, 811.353868713068, 829.858325115361, 848.167780637912,
    865.852406503755, 882.910014941368, 900.572909625516 };

  //These come from OG code (i.e. a regression), since this methodology cannot be reproduced with the ISDA Excel plug-in 
  private static final double[][] BUCKETED_CS01 = new double[][] {
    {4.4427035398373995, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {30.027850578136743, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {55.3868828471072, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {28.004936100689893, 52.464948236595475, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-6.710153266364927E-7, 106.11510018594498, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-9.979437415319481E-6, 79.41636911043722, 52.33566754507881, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-1.317158535019125E-5, 52.97574557690081, 104.15671817013728, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-1.0468882705172433E-5, 26.7944726680229, 155.46101027132454, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-6.711020628102915E-7, -1.8735013540549517E-10, 207.95793999132601, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-5.391628116191427E-6, -1.1431845053921919E-5, 155.48183292227526, 77.98016121330839, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-7.011047992167008E-6, -1.5353607274448677E-5, 103.70980157073038, 154.89567837306817, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-5.6666130121563185E-6, -1.2097774171326847E-5, 52.07063235134618, 231.59564022126253, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-6.711055322572435E-7, -1.734723475976807E-10, -3.122502256758253E-11, 308.9177914019532, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-4.133575426390479E-6, -8.385250827025459E-6, -2.488271394485153E-5, 230.4961297419604, 103.53636431672739, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-5.38333960742321E-6, -1.1411829814456098E-5, -3.383447688687369E-5, 153.2972408699179, 205.42495720608787, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-4.52065468392604E-6, -9.322643351739046E-6, -2.763136247585507E-5, 77.30752137041775, 305.685217218285, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-6.710743072346759E-7, -1.5612511283791264E-10, 3.469446951953614E-12, -6.938893903907228E-12, 407.6505507096366, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-1.5153989174621074E-6, -2.044846930671085E-6, -6.057217227795064E-6, -1.2332784099511329E-5, 303.63313202514536, 128.25265519469943, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-1.90922971876617E-6, -2.9985840199753255E-6, -8.882512780861163E-6, -1.801497839792887E-5, 201.59079309093468, 254.01775321957572, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-1.8757113917633461E-6, -2.917395491852659E-6, -8.642052351515161E-6, -1.750010553136505E-5, 101.48917535185777, 377.34027590482356, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-6.710812461285798E-7, -1.8388068845354155E-10, -3.469446951953614E-12, 0.0, 1.0408340855860843E-11, 502.32007071836574, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-7.254197242900773E-7, -1.3174877855348655E-7, -3.8979236505198855E-7, -7.879599750459931E-7, -1.4433489126108867E-6, 373.4998642080205, 151.76057783829188, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-8.613769419962836E-7, -4.609958248469326E-7, -1.3651475783138523E-6, -2.7597611695906465E-6, -4.660702379588599E-6, 247.5549408099652, 300.0571329812365, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-1.0754938606361009E-6, -9.795740607554393E-7, -2.901383994169393E-6, -5.86546436420754E-6, -9.75627564847592E-6, 124.420283305178, 444.9705302286451, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-6.710604294468681E-7, -1.5959455978986625E-10, 6.938893903907228E-12, 2.0816681711721685E-11, 6.938893903907228E-12, 1.3877787807814457E-11, 591.320974124329, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-1.191567677860661E-6, -1.2606651833557692E-6, -3.7341796321754828E-6, -7.549037783771695E-6, -1.2496996493194246E-5, -1.8668663837040356E-5, 439.6165312641961, 173.3628988450711, 0.0, 0.0, 0.0,
      0.0, 0.0, 0.0 },
    {-1.5417250809335314E-6, -2.108709040715695E-6, -6.246475559024134E-6, -1.2627891787797552E-5, -2.0904784847619595E-5, -3.1030858438363396E-5, 291.6051612433939, 342.39903957818115, 0.0, 0.0,
      0.0, 0.0, 0.0, 0.0 },
    {-1.6227783006250718E-6, -2.3050242270450383E-6, -6.82804507379231E-6, -1.3803645726451208E-5, -2.2851144587665573E-5, -3.384303948195111E-5, 145.59904491918962, 509.0405392005614, 0.0, 0.0, 0.0,
      0.0, 0.0, 0.0 },
    {-6.71067368340772E-7, -1.5265566588595902E-10, -2.7755575615628914E-11, -1.3877787807814457E-11, 1.3877787807814457E-11, 0.0, 0.0, 675.111748106548, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-9.352796315198475E-7, -6.40022757014691E-7, -1.8955670366693766E-6, -3.8321013029474216E-6, -6.343779668238625E-6, -9.377262855103652E-6, -1.283906314597516E-5, 501.4493421292356,
      194.38015272337654, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-9.889242202909543E-7, -7.698841564263148E-7, -2.2803425814288403E-6, -4.609895798424191E-6, -7.631367759941554E-6, -1.1280545941794173E-5, -1.544496475158752E-5, 332.0142602326534,
      383.8860007365982, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-8.419584474061992E-7, -4.1396747141320134E-7, -1.2259429582606174E-6, -2.4783161634012174E-6, -4.10267653183638E-6, -6.064475310818551E-6, -8.303219223293468E-6, 166.69889004964494,
      568.6464971969701, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-6.711090017041954E-7, -1.6653345369377348E-10, -5.551115123125783E-11, -1.3877787807814457E-11, -2.0816681711721685E-11, -4.163336342344337E-11, -2.7755575615628914E-11,
      -2.7755575615628914E-11, 754.813992951027, 0.0, 0.0, 0.0, 0.0, 0.0 },
    {-1.0172487852067036E-6, -8.384404281969182E-7, -2.4833260447998384E-6, -5.020220350537841E-6, -8.310650778664552E-6, -1.2284617767477357E-5, -1.681964290067839E-5, -2.1600034261215484E-5,
      560.4556776565919, 213.9598855949096, 0.0, 0.0, 0.0, 0.0 },
    {-1.0894479762768583E-6, -1.0133421879388038E-6, -3.0014254970289755E-6, -6.067681079802156E-6, -1.0044638731887545E-5, -1.4847754969959936E-5, -2.032900930926118E-5, -2.610677646286419E-5,
      370.95667169668997, 422.386448918767, 0.0, 0.0, 0.0, 0.0 },
    {-9.017231406005521E-7, -5.587127982487061E-7, -1.6546486403257177E-6, -3.3449701342114224E-6, -5.537313663150911E-6, -8.185077615685543E-6, -1.1206688355080985E-5, -1.4391675351443922E-5,
      186.18886987861043, 625.4319165470562, 0.0, 0.0, 0.0, 0.0 },
    {-6.710951239163876E-7, -1.8735013540549517E-10, -5.551115123125783E-11, -5.551115123125783E-11, -1.3877787807814457E-11, -4.85722573273506E-11, -4.85722573273506E-11, -7.632783294297951E-11,
      -4.163336342344337E-11, 829.8597575217836, 0.0, 0.0, 0.0, 0.0 },
    {-8.594444600440454E-7, -4.5632247980265106E-7, -1.3513565266798366E-6, -2.7318564077560836E-6, -4.522333896250785E-6, -6.684756914676626E-6, -9.152484325980481E-6, -1.1753598094799145E-5,
      -1.4752053745237248E-5, 616.0547873050731, 232.37261598402225, 0.0, 0.0, 0.0 },
    {-9.954259638789154E-7, -7.856354455881842E-7, -2.3269858262509047E-6, -4.704139855427059E-6, -7.787395728264812E-6, -1.1511111508433203E-5, -1.576055952412503E-5, -2.023983758370207E-5,
      -2.5042481976988995E-5, 407.6768442722589, 458.61627711250856, 0.0, 0.0, 0.0 },
    {-1.0833695052170356E-6, -9.987149995893674E-7, -2.9581753713259218E-6, -5.980257955506829E-6, -9.89992809952156E-6, -1.463390519873542E-5, -2.0036292069924855E-5, -2.573090351898344E-5,
      -3.166339412885577E-5, 204.57981091090426, 678.9040381522253, 0.0, 0.0, 0.0 },
    {-6.710881850224837E-7, -1.942890293094024E-10, 0.0, 1.3877787807814457E-11, 1.3877787807814457E-11, 0.0, -6.938893903907228E-11, -1.3877787807814457E-11, -4.163336342344337E-11, 0.0,
      900.574329413331, 0.0, 0.0, 0.0 } };

  @Test
  public void cashPaymentTest() {
    final double coupon = COUPON * ONE_BP;

    final int n = MATURITIES.length;
    for (int i = 0; i < n; i++) {
      final CDSAnalytic cds = new CDSAnalytic(TRADE_DATE, EFFECTIVE_DATE, CASH_SETTLE_DATE, STARTDATE, MATURITIES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
      final PointsUpFront puf = PUF_CONVERTER.convert(cds, new QuotedSpread(coupon, QUOTED_SPREADS[i] * ONE_BP), YIELD_CURVE);
      final double cash = (puf.getPointsUpFront() - cds.getAccruedPremium(coupon)) * NOTIONAL;
      // System.out.println(cash);
      assertEquals(CASH_PAYMENTS[i], cash, 1e-0); //no dps given on BBG numbers

      //double check

    }
  }

  @Test
  public void parellelCS01Test() {
    final double coupon = COUPON * ONE_BP;
    final double scale = NOTIONAL * ONE_BP;

    final int n = MATURITIES.length;
    for (int i = 0; i < n; i++) {
      final CDSAnalytic cds = new CDSAnalytic(TRADE_DATE, EFFECTIVE_DATE, CASH_SETTLE_DATE, STARTDATE, MATURITIES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
      final CDSQuoteConvention quote = new QuotedSpread(coupon, QUOTED_SPREADS[i] * ONE_BP);
      final double cs01 = scale * CS01_CAL.parallelCS01(cds, quote, YIELD_CURVE, ONE_BP);
      assertEquals(MATURITIES[i].toString(), PARELLEL_CS01[i], cs01, 1e-14 * NOTIONAL);
    }
  }

  @Test
  //(enabled = false)
  public void bucketedCS01Test() {
    final double scale = NOTIONAL * ONE_BP;

    final int m = BUCKET_DATES.length;
    final CDSAnalytic[] curveCDSs = new CDSAnalytic[m];
    final double[] quotedSpreads = new double[m];
    for (int i = 0; i < m; i++) {
      curveCDSs[i] = new CDSAnalytic(TRADE_DATE, EFFECTIVE_DATE, CASH_SETTLE_DATE, TRADE_DATE, BUCKET_DATES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
      quotedSpreads[i] = QUOTED_SPREADS_AT_BUCKET_DATES[i] * ONE_BP;
    }

    final int n = MATURITIES.length;
    for (int i = 0; i < n; i++) {
      final CDSAnalytic cds = new CDSAnalytic(TRADE_DATE, EFFECTIVE_DATE, CASH_SETTLE_DATE, STARTDATE, MATURITIES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
      final double[] bucketedCS01 = CS01_CAL.bucketedCS01FromQuotedSpreads(cds, COUPON * ONE_BP, YIELD_CURVE, curveCDSs, quotedSpreads, ONE_BP, BumpType.ADDITIVE);

      for (int j = 0; j < m; j++) {
        bucketedCS01[j] *= scale;
        //this is a regression test, so expect exact match - however code changes (i.e. refactoring) mains machine precision is fine 
        assertEquals(MATURITIES[i].toString() + "\t" + BUCKET_DATES[j], BUCKETED_CS01[i][j], bucketedCS01[j], 1e-15 * NOTIONAL);
      }
    }
  }

  @Test
  public void bucketedCS012Test() {
    final int m = BUCKET_DATES.length;
    final CDSAnalytic[] curveCDSs = new CDSAnalytic[m];
    final double[] quotedSpreads = new double[m];
    for (int i = 0; i < m; i++) {
      curveCDSs[i] = new CDSAnalytic(TRADE_DATE, EFFECTIVE_DATE, CASH_SETTLE_DATE, TRADE_DATE, BUCKET_DATES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
      quotedSpreads[i] = QUOTED_SPREADS_AT_BUCKET_DATES[i] * ONE_BP;
    }

    final int n = MATURITIES.length;
    final CDSAnalytic[] tradedCDSs = new CDSAnalytic[n];
    for (int i = 0; i < n; i++) {
      tradedCDSs[i] = new CDSAnalytic(TRADE_DATE, EFFECTIVE_DATE, CASH_SETTLE_DATE, STARTDATE, MATURITIES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
    }
    final double[][] cs01Mat = CS01_CAL.bucketedCS01FromQuotedSpreads(tradedCDSs, COUPON * ONE_BP, YIELD_CURVE, curveCDSs, quotedSpreads, ONE_BP, BumpType.ADDITIVE);

    for (int i = 0; i < n; i++) {
      final double[] bucketedCS01 = CS01_CAL.bucketedCS01FromQuotedSpreads(tradedCDSs[i], COUPON * ONE_BP, YIELD_CURVE, curveCDSs, quotedSpreads, ONE_BP, BumpType.ADDITIVE);

      for (int j = 0; j < m; j++) {
        //this is a regression test, so expect exact match 
        assertEquals(MATURITIES[i].toString() + "\t" + BUCKET_DATES[j], bucketedCS01[j], cs01Mat[i][j]);
      }
    }
  }

  @Test(enabled = false)
  public void bucketedCS01Print() {
    System.out.println("CS01FromQuotedSpreadsTest.bucketedCS01Print disabled test before push");

    final double scale = NOTIONAL * ONE_BP;

    final int m = BUCKET_DATES.length;
    final CDSAnalytic[] curveCDSs = new CDSAnalytic[m];
    final double[] quotedSpreads = new double[m];
    for (int i = 0; i < m; i++) {
      curveCDSs[i] = new CDSAnalytic(TRADE_DATE, EFFECTIVE_DATE, CASH_SETTLE_DATE, TRADE_DATE, BUCKET_DATES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
      quotedSpreads[i] = QUOTED_SPREADS_AT_BUCKET_DATES[i] * ONE_BP;
    }

    final int n = MATURITIES.length;
    final CDSAnalytic[] tradedCDSs = new CDSAnalytic[n];
    for (int i = 0; i < n; i++) {
      tradedCDSs[i] = new CDSAnalytic(TRADE_DATE, EFFECTIVE_DATE, CASH_SETTLE_DATE, STARTDATE, MATURITIES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
    }
    final double[][] res = CS01_CAL.bucketedCS01FromQuotedSpreads(tradedCDSs, COUPON * ONE_BP, YIELD_CURVE, curveCDSs, quotedSpreads, ONE_BP, BumpType.ADDITIVE);

    System.out.print("{");
    for (int i = 0; i < n; i++) {
      System.out.print("{");
      for (int j = 0; j < m - 1; j++) {
        System.out.print(scale * res[i][j] + ", ");
      }
      System.out.print(scale * res[i][m - 1] + "}");
      if (i < n - 1) {
        System.out.print(",\n");
      } else {
        System.out.print("}\n");
      }
    }

  }

}
