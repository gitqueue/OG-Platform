/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.trace;

import java.lang.reflect.Method;

/**
 * Interface for classes that can trace function calls.
 *
 * possible implementations
 * FullTracer - records all calls with return values and args
 * AggregatingTracer - counts invocations to the same method. linked map impl
 * MergingTracer - merges adjacent invocations that are the same (e.g. loops)
 */
/* package */ interface Tracer {

  /* package */ void called(Method method, Object[] args);

  /* package */ void returned(Object returnValue);

  /* package */ void threw(Throwable e);

  /* package */ Call getRoot();
}
