//  ZDT2.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//




//




// 



package org.uma.jmetal.problem.multiobjective.zdt;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/** Class representing problem ZDT2 */
@SuppressWarnings("serial")
public class ZDT2 extends AbstractDoubleProblem {

  /** Constructor. Creates default instance of problem ZDT2 (30 decision variables) */
  public ZDT2()  {
    this(30);
  }

  /**
   * Constructor.
   * Creates a new ZDT2 problem instance.
   *
   * @param numberOfVariables Number of variables
   */
  public ZDT2(Integer numberOfVariables) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(2);
    setName("ZDT2");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  public void evaluate(DoubleSolution solution) {
    double[] f = new double[getNumberOfObjectives()];

    f[0] = solution.getVariableValue(0);
    double g = this.evalG(solution);
    double h = this.evalH(f[0], g);
    f[1] = h * g;

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  /**
   * Returns the value of the ZDT2 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution) {
    double g = 0.0;
    for (int i = 1; i < solution.getNumberOfVariables(); i++) {
      g += solution.getVariableValue(i);
    }
    double constant = 9.0 / (solution.getNumberOfVariables() - 1);
    g = constant * g;
    g = g + 1.0;
    return g;
  }

  /**
   * Returns the value of the ZDT2 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  public double evalH(double f, double g) {
    double h ;
    h = 1.0 - Math.pow(f / g, 2.0);
    return h;
  }
}
