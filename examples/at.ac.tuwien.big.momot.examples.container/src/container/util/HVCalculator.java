package container.util;

import at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension;
import at.ac.tuwien.big.momot.ModuleManager;
import at.ac.tuwien.big.momot.TransformationSearchOrchestration;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.search.fitness.EGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.IEGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.dimension.AbstractEGraphFitnessDimension;
import at.ac.tuwien.big.momot.search.fitness.dimension.TransformationLengthDimension;
import at.ac.tuwien.big.momot.util.MomotUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.Hypervolume;

import container.ContainerModel;
import container.ContainerPackage;
import container.demo.SOContainerSearch;
import container.demo.ContainerUtils;

/**
 * Utility file to compute the hypervolumes obtained over recorded populations
 * in experiment runs. Output files are generated in same folder as set with CASE_PATH constant
 * and contains the summary.txt and hvs.csv.
 *
 * summary.txt:
 *
 * => Summary statistics concerning hypervolume (average, best, ..) and pareto solution
 * set over all runs per algorithm
 *
 * hvs.csv:
 *
 * => Contains the average hypervolumes calculated for each population over all runs. Could be used
 * for plotting HV development over runtime per algorithm
 * E.g., for 5 populations and 3 algorithms and X trials, there will be 5 rows (1 row
 * is 1.population average HV, 2. row is 2. population average HV etc.),
 * 3 columns (1 per algorithm), whereby each value is the average over X trials.
 */
public class HVCalculator {

   private static String CASE_PATH = "output/populations/PG_3S8C/";
   private static boolean INCLUDE_INITIAL_MODEL_SOLUTION = false;

   protected static final String[] modules = new String[] { "transformations/container.henshin" };

   protected static IFitnessDimension<TransformationSolution> _createObjective_0(
         final TransformationSearchOrchestration orchestration) {
      final IFitnessDimension<TransformationSolution> dimension = _createObjectiveHelper_0();
      dimension.setName("SolutionLength");
      dimension.setFunctionType(at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Minimum);
      return dimension;
   }

   protected static IFitnessDimension<TransformationSolution> _createObjective_1(
         final TransformationSearchOrchestration orchestration) {
      return new AbstractEGraphFitnessDimension("RetrievedContainers",
            at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Maximum) {
         @Override
         protected double internalEvaluate(final TransformationSolution solution) {
            final EGraph graph = solution.execute();
            final EObject root = MomotUtil.getRoot(graph);
            return _createObjectiveHelper_1(solution, graph, root);
         }
      };
   }

   protected static IFitnessDimension<TransformationSolution> _createObjective_2(
         final TransformationSearchOrchestration orchestration) {
      return new AbstractEGraphFitnessDimension("RetrievedContainres",
            at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Maximum) {
         @Override
         protected double internalEvaluate(final TransformationSolution solution) {
            final EGraph graph = solution.execute();
            final EObject root = MomotUtil.getRoot(graph);
            return _createObjectiveHelper_2(solution, graph, root);
         }
      };
   }

   protected static IFitnessDimension<TransformationSolution> _createObjectiveHelper_0() {
      final TransformationLengthDimension _transformationLengthDimension = new TransformationLengthDimension();
      return _transformationLengthDimension;
   }

   protected static double _createObjectiveHelper_1(final TransformationSolution solution, final EGraph graph,
         final EObject root) {
      return ContainerUtils.calculateRetrievedContainers((ContainerModel) root);
   }

   // protected static double _createObjectiveHelper_1(final TransformationSolution solution, final EGraph graph,
   // final EObject root) {
   // return ContainerUtils.containerIndex((ContainerModel) root);
   // }

   protected static double _createObjectiveHelper_2(final TransformationSolution solution, final EGraph graph,
         final EObject root) {
      return ContainerUtils.noRetrievedContainers((ContainerModel) root);
   }

   public static Double[] averageRow(final double[][] a2) {
      double rowTotal = 0;
      double average = 0;
      final Double[] averages = new Double[a2.length];

      int i = 0;
      for(final double[] element : a2) {
         int div = 0;
         for(final double element2 : element) {
            rowTotal += element2;
            if(element2 > 0) {
               div++;
            }

         }
         average = rowTotal / div; // calc average
         // System.out.println(average); // print the row average
         averages[i++] = average;
         rowTotal = 0; // start over (for next row)
      }
      return averages;
   }

   private static NondominatedPopulation buildReferenceSet(final String experimentPath) {
      final NondominatedPopulation ref = new NondominatedPopulation();
      FileInputStream fout = null;
      ObjectInputStream oos = null;
      List<double[]> popList = null;

      final String[] algorithmPaths = new File(experimentPath)
            .list((current, name) -> new File(current, name).isDirectory());

      for(final String algorithmPath : algorithmPaths) {
         final String algoPath = experimentPath + algorithmPath + "/runs";
         final File file = new File(algoPath);
         final String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

         for(final String dir : directories) {
            final File curRun = new File(algoPath + "/" + dir);
            final List<String> curPops = Arrays.asList(curRun.list());
            Collections.sort(curPops, new Comparator<String>() {
               @Override
               public int compare(final String o1, final String o2) {
                  return extractInt(o1) - extractInt(o2);
               }

               int extractInt(final String s) {
                  final String num = s.replaceAll("\\D", "");
                  // return 0 if no digits found
                  return num.isEmpty() ? 0 : Integer.parseInt(num);
               }
            });
            final String lastPopPath = curPops.get(curPops.size() - 1);
            try {
               fout = new FileInputStream(algoPath + "/" + dir + "/" + lastPopPath);
               oos = new ObjectInputStream(fout);
               popList = (List<double[]>) oos.readObject();
               for(final double[] element : popList) {
                  if(!INCLUDE_INITIAL_MODEL_SOLUTION && element[0] == 0) {
                     continue;
                  }

                  ref.add(new Solution(element));
               }
               fout.close();
               oos.close();
            } catch(final IOException | ClassNotFoundException e) {
               e.printStackTrace();
            } finally {

            }

         }

      }
      return ref;

   }

   public static StringBuilder computeStatistics(final Problem problem, final NondominatedPopulation referenceSet,
         final String experimentPath, final StringBuilder sb, final String hvSavePath) {
      final Hypervolume h = new Hypervolume(problem, referenceSet);

      FileInputStream fout = null;
      ObjectInputStream oos = null;
      List<double[]> popList = null;
      final List<Double[]> avgHs = new ArrayList<>();
      final StringBuilder sbHV = new StringBuilder();
      final StringBuilder sbFinalHvs = new StringBuilder();

      final String[] algorithmPaths = new File(experimentPath)
            .list((current, name) -> new File(current, name).isDirectory());

      for(final String algorithmPath : algorithmPaths) {
         final String algoPath = experimentPath + algorithmPath + "/runs";
         final File file = new File(algoPath);
         final String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
         PrintWriter allRunsHVWriter = null;
         try {
            allRunsHVWriter = new PrintWriter(new File(experimentPath + algorithmPath + "/all_runs_hvs.csv"));
         } catch(final FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }

         final double[] finalHvs = new double[directories.length];
         final File firstAlgoDir = new File(algoPath + "/" + directories[0]);
         final double[][] hvs = new double[100][directories.length];
         final double[][] bov = new double[firstAlgoDir.list().length][directories.length];
         Arrays.stream(bov).forEach(a -> Arrays.fill(a, Double.POSITIVE_INFINITY));

         final double[][] popTimes = new double[firstAlgoDir.list().length][directories.length];
         final double[] convergedIter = new double[directories.length];

         int i = 0;

         final NondominatedPopulation curNonDomSet = new NondominatedPopulation();

         for(final String dir : directories) {
            final File curRun = new File(algoPath + "/" + dir);
            final List<String> curPops = Arrays.asList(curRun.list());

            Collections.sort(curPops, new Comparator<String>() {
               @Override
               public int compare(final String o1, final String o2) {
                  return extractInt(o1) - extractInt(o2);
               }

               int extractInt(final String s) {
                  final String num = s.replaceAll("\\D", "");
                  // return 0 if no digits found
                  return num.isEmpty() ? 0 : Integer.parseInt(num);
               }
            });
            final NondominatedPopulation curNonDomPop = new NondominatedPopulation();

            double maxHV = 0;
            int maxIterPop = 0;
            int curIterPop = 0;
            int PopIdx = 0;

            for(final String pop : curPops) {
               try {
                  fout = new FileInputStream(algoPath + "/" + dir + "/" + pop);
                  curIterPop++;
                  oos = new ObjectInputStream(fout);
                  popList = (List<double[]>) oos.readObject();
                  for(final double[] element : popList) {
                     if(!INCLUDE_INITIAL_MODEL_SOLUTION && element[0] == 0) {
                        continue;
                     }
                     curNonDomPop.add(new Solution(element));
                     if(bov[PopIdx][i] > element[1]) {
                        bov[PopIdx][i] = element[1];
                     }
                  }

                  final double curHV = h.evaluate(curNonDomPop);
                  final double curFileTime = new File(algoPath + "/" + dir + "/" + pop).lastModified();
                  final double firstFileTime = new File(algoPath + "/" + dir + "/" + curPops.get(0)).lastModified();

                  popTimes[PopIdx][i] = (curFileTime - firstFileTime) / 1000.0;
                  hvs[PopIdx++][i] = curHV;

                  if(curHV > maxHV) {
                     maxHV = curHV;
                     maxIterPop = curIterPop * 100;
                  }
                  fout.close();
                  oos.close();
               } catch(IOException | ClassNotFoundException e) {
                  e.printStackTrace();
               }

            }
            convergedIter[i] = maxIterPop;
            final String lastPopPath = curPops.get(curPops.size() - 1);
            try {
               fout = new FileInputStream(algoPath + "/" + dir + "/" + lastPopPath);
               oos = new ObjectInputStream(fout);
               popList = (List<double[]>) oos.readObject();
               for(final double[] element : popList) {
                  if(!INCLUDE_INITIAL_MODEL_SOLUTION && element[0] == 0) {
                     continue;
                  }

                  curNonDomPop.add(new Solution(element));
                  curNonDomSet.add(new Solution(element));
               }
               fout.close();
               oos.close();

            } catch(final IOException | ClassNotFoundException e) {
               e.printStackTrace();
            }

            finalHvs[i++] = h.evaluate(curNonDomPop);

         }
         sb.append("\n" + algorithmPath + "\n\n");
         sb.append("Approximation Set:\n");
         for(final Solution s : curNonDomSet) {
            sb.append(Arrays.toString(s.getObjectives()) + "\n");
         }

         final StandardDeviation sd = new StandardDeviation();
         final DoubleSummaryStatistics stat = Arrays.stream(finalHvs).summaryStatistics();
         sb.append("Average: " + stat.getAverage() + "\n");
         sb.append("Standard Deviation: " + sd.evaluate(finalHvs) + "\n");
         sb.append("Max: " + stat.getMax() + "\n");
         sb.append("Min: " + stat.getMin() + "\n");
         sb.append("Count: " + stat.getCount() + "\n");
         sb.append("Converged after iter. (avg): " + Arrays.stream(convergedIter).summaryStatistics().getAverage()
               + "\n\n");

         final StringBuilder allRunsHVSB = new StringBuilder();

         allRunsHVSB.append("hv;time;bov;trial;population\n");

         for(int cls = 0; cls < hvs[0].length; cls++) {
            for(int rs = 0; rs < hvs.length; rs++) {
               // Hypervolume per population
               // allRunsHVSB.append(hvs[rs][cls] + ";" + popTimes[rs][cls] + ";" + cls + ";" + rs + "\n");
               // BOV per population
               allRunsHVSB
                     .append(hvs[rs][cls] + ";" + popTimes[rs][cls] + ";" + bov[rs][cls] + ";" + cls + ";" + rs + "\n");
            }
         }

         allRunsHVWriter.write(allRunsHVSB.toString());

         for(final double hv : finalHvs) {
            sbFinalHvs.append(algorithmPath + ";" + hv + "\n");
         }

         final Double[] avgHvs = averageRow(hvs);
         avgHs.add(avgHvs);

         if(algorithmPath.compareTo(algorithmPaths[algorithmPaths.length - 1]) == 0) {
            sbHV.append(algorithmPath + "\n");

         } else {
            sbHV.append(algorithmPath + ";");
         }

      }

      for(int i = 0; i < avgHs.get(0).length; i++) {
         for(int j = 0; j < avgHs.size(); j++) {
            if(j == avgHs.size() - 1) {
               sbHV.append(avgHs.get(j)[i] + "\n");
            } else {
               sbHV.append(avgHs.get(j)[i] + ";");
            }
         }

      }

      try {
         final PrintWriter writer = new PrintWriter(new File(hvSavePath + "hvs.csv"));

         writer.write(sbHV.toString());
         writer.close();

         final PrintWriter hvWriter = new PrintWriter(new File(hvSavePath + "final_hvs.csv"));

         hvWriter.write(sbFinalHvs.toString());
         hvWriter.close();

      } catch(final IOException e) {
         e.printStackTrace();
      }

      return sb;

   }

   protected static IEGraphMultiDimensionalFitnessFunction createFitnessFunction(
         final TransformationSearchOrchestration orchestration) {
      final IEGraphMultiDimensionalFitnessFunction function = new EGraphMultiDimensionalFitnessFunction();
      function.addObjective(_createObjective_1(orchestration));
      function.addObjective(_createObjective_0(orchestration));
      // function.addObjective(_createObjective_2(orchestration));

      return function;
   }

   protected static EGraph createInputGraph(final String initialGraph, final ModuleManager moduleManager) {
      final EGraph graph = moduleManager.loadGraph(initialGraph);
      return graph;
   }

   protected static ModuleManager createModuleManager() {
      final ModuleManager manager = new ModuleManager();
      for(final String module : modules) {
         manager.addModule(URI.createFileURI(new File(module).getPath().toString()).toString());
      }
      return manager;
   }

   public static void main(final String... args) throws IOException, ClassNotFoundException {

      ContainerPackage.eINSTANCE.eClass();

      final SOContainerSearch search = new SOContainerSearch();
      final String initialGraph = "model/3S_8C.xmi";

      final TransformationSearchOrchestration orchestration = new TransformationSearchOrchestration();
      final ModuleManager moduleManager = createModuleManager();
      final EGraph graph = createInputGraph(initialGraph, moduleManager);
      orchestration.setModuleManager(moduleManager);
      orchestration.setProblemGraph(graph);
      orchestration.setSolutionLength(1);
      orchestration.setFitnessFunction(createFitnessFunction(orchestration));

      final NondominatedPopulation referenceSet = buildReferenceSet(CASE_PATH);
      System.out.println("Calculated and saved summary statistics at " + CASE_PATH);
      StringBuilder sb = new StringBuilder();

      sb.append("Reference Set:\n\n");
      for(final Solution s : referenceSet) {
         for(final double v : s.getObjectives()) {
            sb.append(v + " ");
         }
         sb.append("\n");
      }
      sb = computeStatistics(orchestration.getProblem(), referenceSet, CASE_PATH, sb, CASE_PATH);

      Files.write(Paths.get(CASE_PATH + "summary.txt"), sb.toString().getBytes());

   }

}
