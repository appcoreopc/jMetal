//  MOCellRunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.multiobjective.mocell.MOCellTemplate;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.operator.selection.BinaryTournament;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.io.IOException;

/**
 * Class to configure and execute the MOCell algorithm (variant: AsyncMOCell2)
 */

public class MOCellRunner {
  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws java.lang.ClassNotFoundException
   * Usage: three options
   *       - org.uma.jmetal.runner.multiobjective.MOCellRunner
   *       - org.uma.jmetal.runner.multiobjective.MOCellRunner problemName
   *       - org.uma.jmetal.runner.multiobjective.MOCellRunner problemName paretoFrontFile
   */
  public static void main(String[] args) throws
          JMetalException, SecurityException, IOException, ClassNotFoundException {

    Problem problem;
    Algorithm algorithm;
    Crossover crossover;
    Mutation mutation;
    Selection selection;

    QualityIndicatorGetter indicators;

    indicators = null;
    if (args.length == 1) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
    } else if (args.length == 2) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
      indicators = new QualityIndicatorGetter(problem, args[1]);
    } else {
      problem = new Kursawe("Real", 3);
      /*
        Examples:
        problem = new Water("Real");
        problem = new ZDT3("ArrayReal", 30);
        problem = new ConstrEx("Real");
        problem = new DTLZ1("Real");
        problem = new OKA2("Real")
      */
    }
    crossover = new SBXCrossover.Builder()
            .setProbability(0.9)
            .setDistributionIndex(20.0)
            .build();

    mutation = new PolynomialMutation.Builder()
            .setProbability(1.0 / problem.getNumberOfVariables())
            .setDistributionIndex(20.0)
            .build();

    selection = new BinaryTournament.Builder()
            .build() ;

    algorithm = new MOCellTemplate.Builder(problem)
            .setPopulationSize(100)
            .setArchiveSize(100)
            .setMaxEvaluations(25000)
            .setNumberOfFeedbackSolutionsFromArchive(20)
            .setCrossover(crossover)
            .setMutation(mutation)
            .setSelection(selection)
            .build("AsyncMOCell2");

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionSetOutput.Printer(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    if (indicators != null) {
      JMetalLogger.logger.info("Quality indicators");
      JMetalLogger.logger.info("Hypervolume: " + indicators.getHypervolume(population));
      JMetalLogger.logger.info("GD         : " + indicators.getGD(population));
      JMetalLogger.logger.info("IGD        : " + indicators.getIGD(population));
      JMetalLogger.logger.info("Spread     : " + indicators.getSpread(population));
      JMetalLogger.logger.info("Epsilon    : " + indicators.getEpsilon(population));
    }
  }
}