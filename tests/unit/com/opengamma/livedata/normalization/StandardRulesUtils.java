/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.normalization;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.fudgemsg.FudgeField;
import org.fudgemsg.FudgeFieldContainer;

import com.google.common.collect.Sets;

/**
 * 
 *
 * @author pietari
 */
public class StandardRulesUtils {
  
  public static void validateOpenGammaMsg(FudgeFieldContainer msg) {
    assertNotNull(msg);
    
    Set<String> acceptableFields = Sets.newHashSet(
        MarketDataRequirementNames.INDICATIVE_VALUE,
        MarketDataRequirementNames.VOLUME,
        MarketDataRequirementNames.IMPLIED_VOLATILITY);
    for (FudgeField field : msg.getAllFields()) {
      assertTrue(acceptableFields + " does not contain " + field.getName(), acceptableFields.contains(field.getName()));
    }
    
    assertNotNull(msg.getDouble(MarketDataRequirementNames.INDICATIVE_VALUE));
    assertTrue(msg.getDouble(MarketDataRequirementNames.INDICATIVE_VALUE) >= 0.0);
    
    if (msg.getDouble(MarketDataRequirementNames.IMPLIED_VOLATILITY) != null) {
      assertTrue(msg.getDouble(MarketDataRequirementNames.IMPLIED_VOLATILITY) >= 0.0);
    }
  }

}
