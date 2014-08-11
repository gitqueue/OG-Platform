/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.sabr;

import java.util.HashMap;
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

import com.google.common.collect.ImmutableMap;
import com.opengamma.core.config.Config;
import com.opengamma.financial.convention.SwapFixedLegConvention;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;
import com.opengamma.util.result.FailureStatus;
import com.opengamma.util.result.Result;

/**
 * Joda bean config object that allows retrieval of a SabrSurfaceSelector
 * for a currency.
 */
@BeanDefinition
@Config(group = "SABR Params", description = "SABR Data Configuration")
public final class SabrSwaptionDataConfig implements ImmutableBean {

  @PropertyDefinition(validate = "notNull")
  private final Map<Currency, SabrSurfaceSelector<SwapFixedLegConvention, SabrExpiryTenorSurface>> _currencyMap;

  @ImmutableConstructor
  SabrSwaptionDataConfig(Map<Currency, SabrSurfaceSelector<SwapFixedLegConvention, SabrExpiryTenorSurface>> currencyMap) {
    ArgumentChecker.notEmpty(currencyMap, "currencyMap");
    _currencyMap = ImmutableMap.copyOf(currencyMap);
  }

  /**
   * Retrieve the {@link SabrSurfaceSelector} holding SABR surfaces
   * for a particular currency.
   *
   * @param currency currency to find the {@link SabrSurfaceSelector} for, not null
   * @return the {@link SabrSurfaceSelector} for the currency if available
   */
  public Result<SabrSurfaceSelector<SwapFixedLegConvention, SabrExpiryTenorSurface>> findSurfaceSelector(Currency currency) {
    if (_currencyMap.containsKey(currency)) {
      return Result.success(_currencyMap.get(currency));
    } else {
      return Result.failure(FailureStatus.MISSING_DATA, "No SABR Surface selector found for currency: {}", currency);
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SabrSwaptionDataConfig}.
   * @return the meta-bean, not null
   */
  public static SabrSwaptionDataConfig.Meta meta() {
    return SabrSwaptionDataConfig.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(SabrSwaptionDataConfig.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static SabrSwaptionDataConfig.Builder builder() {
    return new SabrSwaptionDataConfig.Builder();
  }

  @Override
  public SabrSwaptionDataConfig.Meta metaBean() {
    return SabrSwaptionDataConfig.Meta.INSTANCE;
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
   * Gets the currencyMap.
   * @return the value of the property, not null
   */
  public Map<Currency, SabrSurfaceSelector<SwapFixedLegConvention, SabrExpiryTenorSurface>> getCurrencyMap() {
    return _currencyMap;
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
      SabrSwaptionDataConfig other = (SabrSwaptionDataConfig) obj;
      return JodaBeanUtils.equal(getCurrencyMap(), other.getCurrencyMap());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getCurrencyMap());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("SabrSwaptionDataConfig{");
    buf.append("currencyMap").append('=').append(JodaBeanUtils.toString(getCurrencyMap()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SabrSwaptionDataConfig}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code currencyMap} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Map<Currency, SabrSurfaceSelector<SwapFixedLegConvention, SabrExpiryTenorSurface>>> _currencyMap = DirectMetaProperty.ofImmutable(
        this, "currencyMap", SabrSwaptionDataConfig.class, (Class) Map.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "currencyMap");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 586610571:  // currencyMap
          return _currencyMap;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public SabrSwaptionDataConfig.Builder builder() {
      return new SabrSwaptionDataConfig.Builder();
    }

    @Override
    public Class<? extends SabrSwaptionDataConfig> beanType() {
      return SabrSwaptionDataConfig.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code currencyMap} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Map<Currency, SabrSurfaceSelector<SwapFixedLegConvention, SabrExpiryTenorSurface>>> currencyMap() {
      return _currencyMap;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 586610571:  // currencyMap
          return ((SabrSwaptionDataConfig) bean).getCurrencyMap();
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
   * The bean-builder for {@code SabrSwaptionDataConfig}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<SabrSwaptionDataConfig> {

    private Map<Currency, SabrSurfaceSelector<SwapFixedLegConvention, SabrExpiryTenorSurface>> _currencyMap = new HashMap<Currency, SabrSurfaceSelector<SwapFixedLegConvention, SabrExpiryTenorSurface>>();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(SabrSwaptionDataConfig beanToCopy) {
      this._currencyMap = new HashMap<Currency, SabrSurfaceSelector<SwapFixedLegConvention, SabrExpiryTenorSurface>>(beanToCopy.getCurrencyMap());
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 586610571:  // currencyMap
          return _currencyMap;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 586610571:  // currencyMap
          this._currencyMap = (Map<Currency, SabrSurfaceSelector<SwapFixedLegConvention, SabrExpiryTenorSurface>>) newValue;
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
    public SabrSwaptionDataConfig build() {
      return new SabrSwaptionDataConfig(
          _currencyMap);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code currencyMap} property in the builder.
     * @param currencyMap  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder currencyMap(Map<Currency, SabrSurfaceSelector<SwapFixedLegConvention, SabrExpiryTenorSurface>> currencyMap) {
      JodaBeanUtils.notNull(currencyMap, "currencyMap");
      this._currencyMap = currencyMap;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("SabrSwaptionDataConfig.Builder{");
      buf.append("currencyMap").append('=').append(JodaBeanUtils.toString(_currencyMap));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
