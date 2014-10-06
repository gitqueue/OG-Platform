/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.server;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Iterables;
import com.opengamma.sesame.engine.Results;

/**
 * Server capable of executing view requests.
 */
public class DefaultFunctionServer implements FunctionServer {

  /**
   * Factory which will create cycle runners which will run
   * a view to completion.
   */
  private final CycleRunnerFactory _cycleRunnerFactory;

  /**
   * Construct the server.
   *
   * @param cycleRunnerFactory factory which will create cycle
   * runners which will run a view to completion
   */
  public DefaultFunctionServer(CycleRunnerFactory cycleRunnerFactory) {
    _cycleRunnerFactory = cycleRunnerFactory;
  }

  @Override
  public Results executeSingleCycle(FunctionServerRequest<IndividualCycleOptions> request) {

    ResultsHolder handler = new ResultsHolder();
    _cycleRunnerFactory.createDirectCycleRunner(request, handler).execute();
    return Iterables.getOnlyElement(handler.getResults());
  }

  @Override
  public List<Results> executeMultipleCycles(FunctionServerRequest<GlobalCycleOptions> request) {

    ResultsHolder handler = new ResultsHolder();
    _cycleRunnerFactory.createDirectCycleRunner(request, handler).execute();
    return handler.getResults();
  }

  /**
   * Simple class to collect up the results generated by each
   * individual cycle.
   */
  private static class ResultsHolder implements CycleResultsHandler {

    /**
     * The list of results received from cycle executions. The
     * order of the results matches the order of the cycle
     * options from the original request.
     */
    private final List<Results> _results = new ArrayList<>();

    @Override
    public void handleResults(Results results) {
      _results.add(results);
    }

    /**
     * Gets the results received from cycle executions.
     * @return the results, not null
     */
    public List<Results> getResults() {
      return _results;
    }
  }
}
