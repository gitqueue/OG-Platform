/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.marketdata.scenarios;

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

import com.google.common.collect.ImmutableSet;
import com.opengamma.sesame.MulticurveBundle;
import com.opengamma.sesame.marketdata.MarketDataId;
import com.opengamma.sesame.marketdata.MulticurveId;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;

/**
 * Filter that matches individual curves in a {@link MulticurveBundle} based on the curve's currency.
 */
@BeanDefinition
public final class CurrencyMulticurveFilter implements MarketDataFilter, ImmutableBean {

  /** The currency matched by this filter. */
  @PropertyDefinition(validate = "notNull")
  private final Currency _currency;

  /**
   * @param currency the currency matched by this filter
   */
  @ImmutableConstructor
  public CurrencyMulticurveFilter(Currency currency) {
    _currency = ArgumentChecker.notNull(currency, "currency");
  }

  @Override
  public Set<MulticurveMatchDetails> apply(MarketDataId<?> marketDataId) {
    MulticurveId id = (MulticurveId) marketDataId;
    MulticurveMetadata metadata = MulticurveMetadata.forConfiguration(id.resolveConfig());
    return apply(metadata);
  }

  @Override
  public Set<MulticurveMatchDetails> apply(MarketDataId<?> marketDataId, Object marketData) {
    MulticurveBundle multicurve = (MulticurveBundle) marketData;
    MulticurveMetadata metadata = MulticurveMetadata.forMulticurve(multicurve);
    return apply(metadata);
  }

  private Set<MulticurveMatchDetails> apply(MulticurveMetadata metadata) {
    Set<String> curveNames = metadata.getCurveNamesByCurrency().get(_currency);
    ImmutableSet.Builder<MulticurveMatchDetails> builder = ImmutableSet.builder();

    for (String curveName : curveNames) {
      builder.add(StandardMatchDetails.multicurve(curveName));
    }
    return builder.build();
  }

  @Override
  public Class<?> getMarketDataType() {
    return MulticurveBundle.class;
  }

  @Override
  public Class<MulticurveId> getMarketDataIdType() {
    return MulticurveId.class;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CurrencyMulticurveFilter}.
   * @return the meta-bean, not null
   */
  public static CurrencyMulticurveFilter.Meta meta() {
    return CurrencyMulticurveFilter.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CurrencyMulticurveFilter.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static CurrencyMulticurveFilter.Builder builder() {
    return new CurrencyMulticurveFilter.Builder();
  }

  @Override
  public CurrencyMulticurveFilter.Meta metaBean() {
    return CurrencyMulticurveFilter.Meta.INSTANCE;
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
   * Gets the currency matched by this filter.
   * @return the value of the property, not null
   */
  public Currency getCurrency() {
    return _currency;
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
      CurrencyMulticurveFilter other = (CurrencyMulticurveFilter) obj;
      return JodaBeanUtils.equal(getCurrency(), other.getCurrency());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getCurrency());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("CurrencyMulticurveFilter{");
    buf.append("currency").append('=').append(JodaBeanUtils.toString(getCurrency()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CurrencyMulticurveFilter}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<Currency> _currency = DirectMetaProperty.ofImmutable(
        this, "currency", CurrencyMulticurveFilter.class, Currency.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "currency");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          return _currency;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public CurrencyMulticurveFilter.Builder builder() {
      return new CurrencyMulticurveFilter.Builder();
    }

    @Override
    public Class<? extends CurrencyMulticurveFilter> beanType() {
      return CurrencyMulticurveFilter.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Currency> currency() {
      return _currency;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          return ((CurrencyMulticurveFilter) bean).getCurrency();
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
   * The bean-builder for {@code CurrencyMulticurveFilter}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<CurrencyMulticurveFilter> {

    private Currency _currency;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(CurrencyMulticurveFilter beanToCopy) {
      this._currency = beanToCopy.getCurrency();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          return _currency;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          this._currency = (Currency) newValue;
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
    public CurrencyMulticurveFilter build() {
      return new CurrencyMulticurveFilter(
          _currency);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code currency} property in the builder.
     * @param currency  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder currency(Currency currency) {
      JodaBeanUtils.notNull(currency, "currency");
      this._currency = currency;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("CurrencyMulticurveFilter.Builder{");
      buf.append("currency").append('=').append(JodaBeanUtils.toString(_currency));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
