/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.analytics.financial.timeseries.util.TimeSeriesDifferenceOperator;
import com.opengamma.analytics.financial.timeseries.util.TimeSeriesPercentageChangeOperator;
import com.opengamma.analytics.math.function.Function1D;
import com.opengamma.timeseries.date.DateDoubleTimeSeries;
import com.opengamma.timeseries.date.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.ArgumentChecker;

/**
 * Converts a spot series to a return series based on the supplied difference operator.
 */
@BeanDefinition
public class DifferenceOperatorReturnConverter implements TimeSeriesReturnConverter, ImmutableBean {

  @PropertyDefinition(validate = "notNull")
  private final TimeSeriesReturnConverterFactory.ConversionType _conversionType;

  /**
   * Calculates an absolute return time series
   */
  private final Function1D<DateDoubleTimeSeries<?>, DateDoubleTimeSeries<?>> _differenceOperator;

  /**
   * Constructor with the desired difference operator.
   *
   * @param conversionType  the type of conversion to be done, not null
   */
  @ImmutableConstructor
  public DifferenceOperatorReturnConverter(TimeSeriesReturnConverterFactory.ConversionType conversionType) {
    _conversionType = ArgumentChecker.notNull(conversionType, "conversionType");
    _differenceOperator = conversionType == TimeSeriesReturnConverterFactory.ConversionType.ABSOLUTE ?
        new TimeSeriesDifferenceOperator() :
        new TimeSeriesPercentageChangeOperator();
  }

  @Override
  public LocalDateDoubleTimeSeries convert(LocalDateDoubleTimeSeries spotSeries) {
    return (LocalDateDoubleTimeSeries) _differenceOperator.evaluate(spotSeries);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code DifferenceOperatorReturnConverter}.
   * @return the meta-bean, not null
   */
  public static DifferenceOperatorReturnConverter.Meta meta() {
    return DifferenceOperatorReturnConverter.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(DifferenceOperatorReturnConverter.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static DifferenceOperatorReturnConverter.Builder builder() {
    return new DifferenceOperatorReturnConverter.Builder();
  }

  @Override
  public DifferenceOperatorReturnConverter.Meta metaBean() {
    return DifferenceOperatorReturnConverter.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the conversionType.
   * @return the value of the property, not null
   */
  public TimeSeriesReturnConverterFactory.ConversionType getConversionType() {
    return _conversionType;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      DifferenceOperatorReturnConverter other = (DifferenceOperatorReturnConverter) obj;
      return JodaBeanUtils.equal(getConversionType(), other.getConversionType());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getConversionType());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("DifferenceOperatorReturnConverter{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("conversionType").append('=').append(JodaBeanUtils.toString(getConversionType())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code DifferenceOperatorReturnConverter}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code conversionType} property.
     */
    private final MetaProperty<TimeSeriesReturnConverterFactory.ConversionType> _conversionType = DirectMetaProperty.ofImmutable(
        this, "conversionType", DifferenceOperatorReturnConverter.class, TimeSeriesReturnConverterFactory.ConversionType.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "conversionType");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 989646192:  // conversionType
          return _conversionType;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public DifferenceOperatorReturnConverter.Builder builder() {
      return new DifferenceOperatorReturnConverter.Builder();
    }

    @Override
    public Class<? extends DifferenceOperatorReturnConverter> beanType() {
      return DifferenceOperatorReturnConverter.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code conversionType} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<TimeSeriesReturnConverterFactory.ConversionType> conversionType() {
      return _conversionType;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 989646192:  // conversionType
          return ((DifferenceOperatorReturnConverter) bean).getConversionType();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code DifferenceOperatorReturnConverter}.
   */
  public static class Builder extends DirectFieldsBeanBuilder<DifferenceOperatorReturnConverter> {

    private TimeSeriesReturnConverterFactory.ConversionType _conversionType;

    /**
     * Restricted constructor.
     */
    protected Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    protected Builder(DifferenceOperatorReturnConverter beanToCopy) {
      this._conversionType = beanToCopy.getConversionType();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 989646192:  // conversionType
          return _conversionType;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 989646192:  // conversionType
          this._conversionType = (TimeSeriesReturnConverterFactory.ConversionType) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public DifferenceOperatorReturnConverter build() {
      return new DifferenceOperatorReturnConverter(
          _conversionType);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code conversionType} property in the builder.
     * @param conversionType  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder conversionType(TimeSeriesReturnConverterFactory.ConversionType conversionType) {
      JodaBeanUtils.notNull(conversionType, "conversionType");
      this._conversionType = conversionType;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("DifferenceOperatorReturnConverter.Builder{");
      int len = buf.length();
      toString(buf);
      if (buf.length() > len) {
        buf.setLength(buf.length() - 2);
      }
      buf.append('}');
      return buf.toString();
    }

    protected void toString(StringBuilder buf) {
      buf.append("conversionType").append('=').append(JodaBeanUtils.toString(_conversionType)).append(',').append(' ');
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
