/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.ircurve;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.fudgemsg.FudgeField;
import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.MutableFudgeFieldContainer;
import org.fudgemsg.mapping.FudgeDeserializationContext;
import org.fudgemsg.mapping.FudgeSerializationContext;

import com.opengamma.financial.fudgemsg.FixedIncomeStripBuilder;
import com.opengamma.util.time.Tenor;

/**
 * A fixed income strip.
 */
public class FixedIncomeStrip implements Serializable, Comparable<FixedIncomeStrip> {
  private final StripInstrumentType _instrumentType;
  private final Tenor _curveNodePointTime;
  private final String _conventionName;
  private int _nthFutureFromTenor;

  /**
   * Creates a strip for non-future instruments
   * @param instrumentType the instrument type
   * @param curveNodePointTime the time of the curve node point
   * @param conventionName the name of the convention to use to resolve the strip into a security
   */
  public FixedIncomeStrip(StripInstrumentType instrumentType, Tenor curveNodePointTime, String conventionName) {
    Validate.notNull(instrumentType, "InstrumentType");
    Validate.isTrue(instrumentType != StripInstrumentType.FUTURE);
    Validate.notNull(curveNodePointTime, "Tenor");
    Validate.notNull(conventionName, "ConventionName");
    _instrumentType = instrumentType;
    _curveNodePointTime = curveNodePointTime;
    _conventionName = conventionName;
  }
  
  /**
   * Creates a future strip
   * @param instrumentType the instrument type
   * @param curveNodePointTime the time of the curve node point
   * @param conventionName the name of the convention to use to resolve the strip into a security
   * @param nthFutureFromTenor how many futures to step through from the curveDate + the tenor. 1-based, must be >0.
   *   e.g. 3 (tenor = 1YR) => 3rd quarterly future after curveDate +  1YR.
   *   
   */
  public FixedIncomeStrip(StripInstrumentType instrumentType, Tenor curveNodePointTime, int nthFutureFromTenor, String conventionName) {
    Validate.isTrue(instrumentType == StripInstrumentType.FUTURE);
    Validate.notNull(curveNodePointTime, "Tenor");
    Validate.isTrue(nthFutureFromTenor > 0);
    Validate.notNull(conventionName, "ConventionName");
    _instrumentType = instrumentType;
    _curveNodePointTime = curveNodePointTime;
    _nthFutureFromTenor = nthFutureFromTenor;
    _conventionName = conventionName;
  }
  
  
  /**
   * @return an enum describing the instrument type used to construct this strip
   */
  public StripInstrumentType getInstrumentType() {
    return _instrumentType;
  }
  
  /**
   * @return a tenor representing the time of the curve node point
   */
  public Tenor getCurveNodePointTime() {
    return _curveNodePointTime;
  }
  
  /**
   * Get the number of the quarterly IR futures after the tenor to choose.  
   * NOTE: THIS DOESN'T REFER TO A GENERIC FUTURE
   * @return number of futures after the tenor
   * @throws IllegalStateException if called on a non-future strip
   */
  public int getNumberOfFuturesAfterTenor() {
    if (_instrumentType != StripInstrumentType.FUTURE) {
      throw new IllegalStateException("Cannot get number of futures after tenor for a non future strip " + toString());
    }
    return _nthFutureFromTenor;
  }
  
  /**
   * @return the name of the convention used to resolve this strip definition into a security
   */
  public String getConventionName() {
    return _conventionName;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof FixedIncomeStrip) {
      FixedIncomeStrip other = (FixedIncomeStrip) obj;
      return ObjectUtils.equals(_curveNodePointTime, other._curveNodePointTime) &&
             ObjectUtils.equals(_conventionName, other._conventionName) &&
             _instrumentType == other._instrumentType;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return _curveNodePointTime.hashCode();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  @Override
  public int compareTo(FixedIncomeStrip o) {
    int result = (int) getCurveNodePointTime().getPeriod().toPeriodFields().toEstimatedDuration().compareTo(o.getCurveNodePointTime().getPeriod().toPeriodFields().toEstimatedDuration());
    if (result != 0) {
      return result;
    }
    result = getInstrumentType().ordinal() - o.getInstrumentType().ordinal(); 
    if (result != 0) {
      return result;
    } 
    if (getInstrumentType() == StripInstrumentType.FUTURE) {
      result = getNumberOfFuturesAfterTenor() - o.getNumberOfFuturesAfterTenor();
    }
    return result;
  }

  // REVIEW: jim 22-Aug-2010 -- get rid of these and use the builder directly
  public void toFudgeMsg(final FudgeSerializationContext context, final MutableFudgeFieldContainer message) {
    FixedIncomeStripBuilder builder = new FixedIncomeStripBuilder();
    MutableFudgeFieldContainer container = builder.buildMessage(context, this);
    for (FudgeField field : container.getAllFields()) {
      message.add(field);
    }
  }
  // REVIEW: jim 22-Aug-2010 -- get rid of these and use the builder directly
  public static FixedIncomeStrip fromFudgeMsg(final FudgeDeserializationContext context, final FudgeFieldContainer message) {
    FixedIncomeStripBuilder builder = new FixedIncomeStripBuilder();
    return builder.buildObject(context, message);
  }
}
