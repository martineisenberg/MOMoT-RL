package icmt.tool.momot.demo.architecture;

import at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension;
import at.ac.tuwien.big.moea.util.MathUtil;
import at.ac.tuwien.big.momot.ModuleManager;
import at.ac.tuwien.big.momot.TransformationSearchOrchestration;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.search.fitness.EGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.IEGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.dimension.AbstractEGraphFitnessDimension;
import at.ac.tuwien.big.momot.search.fitness.dimension.TransformationLengthDimension;
import at.ac.tuwien.big.momot.util.MomotUtil;

import com.google.common.base.Objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.xtext.xbase.lib.ExclusiveRange;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.Hypervolume;

import icmt.tool.momot.demo.MOArchitectureSearch;
import icmt.tool.momot.demo.FitnessCalculator;

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

   private static String CASE_PATH = "output/populations/TTC_A/";
   private static boolean INCLUDE_INITIAL_MODEL_SOLUTION = false;

   protected static final String[] modules = new String[] { "transformations/architecture.henshin" };

   protected static IFitnessDimension<TransformationSolution> _createObjective_0(
         final TransformationSearchOrchestration orchestration) {
      return new AbstractEGraphFitnessDimension("CRAIndex",
            at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Maximum) {
         @Override
         protected double internalEvaluate(final TransformationSolution solution) {
            final EGraph graph = solution.execute();
            final EObject root = MomotUtil.getRoot(graph);
            return _createObjectiveHelper_0(solution, graph, root);
         }
      };
   }

   protected static IFitnessDimension<TransformationSolution> _createObjective_2(
         final TransformationSearchOrchestration orchestration) {
      final IFitnessDimension<TransformationSolution> dimension = _createObjectiveHelper_2();
      dimension.setName("SolutionLength");
      dimension.setFunctionType(at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Minimum);
      return dimension;
   }

   protected static double _createObjectiveHelper_0(final TransformationSolution solution, final EGraph graph,
         final EObject root) {
      return FitnessCalculator.calculateCRAIndex((ClassModel) root);
   }

   protected static IFitnessDimension<TransformationSolution> _createObjectiveHelper_2() {
      final TransformationLengthDimension _transformationLengthDimension = new TransformationLengthDimension();
      return _transformationLengthDimension;
   }

   protected static EGraph adaptInputGraph(final ModuleManager moduleManager, final EGraph initialGraph) {
      final EGraph problemGraph = MomotUtil.copy(initialGraph);
      final EObject root = MomotUtil.getRoot(problemGraph);
      return MomotUtil.createEGraph(adaptInputModel(root));
   }

   protected static EObject adaptInputModel(final EObject root) {
      final ClassModel cm = (ClassModel) root;
      final int _size = cm.getFeatures().size();
      final int _size_1 = cm.getClasses().size();
      final int _minus = _size - _size_1;
      final ExclusiveRange _doubleDotLessThan = new ExclusiveRange(0, _minus, true);
      for(final Integer i : _doubleDotLessThan) {
         {
            final icmt.tool.momot.demo.architecture.Class newClass = ArchitectureFactory.eINSTANCE.createClass();
            newClass.setName("Class_" + i);
            cm.getClasses().add(newClass);
         }
      }
      final EList<Feature> _features = cm.getFeatures();
      for(final Feature feature : _features) {
         final icmt.tool.momot.demo.architecture.Class _isEncapsulatedBy = feature.getIsEncapsulatedBy();
         final boolean _equals = Objects.equal(_isEncapsulatedBy, null);
         if(_equals) {
            cm.getClasses().get(MathUtil.randomInteger(cm.getClasses().size())).getEncapsulates().add(feature);
         }
      }
      return cm;
   }

   public static Double[] averageRow(final double[][] a2) {
      double rowTotal = 0;
      double average = 0;
      final Double[] averages = new Double[a2.length];

      int i = 0;
      for(final double[] element : a2) {
         for(final double element2 : element) {
            rowTotal += element2;
         }
         average = rowTotal / element.length; // calc average
         // System.out.println(average); // print the row average
         averages[i++] = average;
         rowTotal = 0; // start over (for next row)
      }
      return averages;
   }

   private static NondominatedPopulation buildReferenceSet(final String experimentPath,
         final String... algorithmPaths) {
      final NondominatedPopulation ref = new NondominatedPopulation();
      FileInputStream fout = null;
      ObjectInputStream oos = null;
      List<double[]> popList = null;

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
                  if(!INCLUDE_INITIAL_MODEL_SOLUTION && element[1] == 0) {
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
         final String experimentPath, final StringBuilder sb, final String hvSavePath, final String... algorithmPaths) {
      final Hypervolume h = new Hypervolume(problem, referenceSet);

      FileInputStream fout = null;
      ObjectInputStream oos = null;
      List<double[]> popList = null;
      final List<Double[]> avgHs = new ArrayList<>();
      final StringBuilder sbHV = new StringBuilder();

      for(final String algorithmPath : algorithmPaths) {
         final String algoPath = experimentPath + algorithmPath + "/runs";
         final File file = new File(algoPath);
         final String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

         final double[] finalHvs = new double[directories.length];
         final File firstAlgoDir = new File(algoPath + "/" + directories[0]);
         final double[][] hvs = new double[firstAlgoDir.list().length][directories.length];

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
                     if(!INCLUDE_INITIAL_MODEL_SOLUTION && element[1] == 0) {
                        continue;
                     }
                     curNonDomPop.add(new Solution(element));

                  }

                  final double curHV = h.evaluate(curNonDomPop);
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
                  if(!INCLUDE_INITIAL_MODEL_SOLUTION && element[1] == 0) {
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

      } catch(final IOException e) {
         e.printStackTrace();
      }

      return sb;

   }

   protected static IEGraphMultiDimensionalFitnessFunction createFitnessFunction(
         final TransformationSearchOrchestration orchestration) {
      final IEGraphMultiDimensionalFitnessFunction function = new EGraphMultiDimensionalFitnessFunction();
      function.addObjective(_createObjective_0(orchestration));
      function.addObjective(_createObjective_2(orchestration));
      return function;
   }

   protected static EGraph createInputGraph(final String initialGraph, final ModuleManager moduleManager) {
      final EGraph graph = moduleManager.loadGraph(initialGraph);
      return adaptInputGraph(moduleManager, graph);
   }

   protected static ModuleManager createModuleManager() {
      final ModuleManager manager = new ModuleManager();
      for(final String module : modules) {
         manager.addModule(URI.createFileURI(new File(module).getPath().toString()).toString());
      }
      return manager;
   }

   public static void main(final String... args) throws IOException, ClassNotFoundException {

      ArchitecturePackage.eINSTANCE.eClass();

      final MOArchitectureSearch search = new MOArchitectureSearch();
      final String initialGraph = "model/TTC_InputRDG_E.xmi";

      final TransformationSearchOrchestration orchestration = new TransformationSearchOrchestration();
      final ModuleManager moduleManager = createModuleManager();
      final EGraph graph = createInputGraph(initialGraph, moduleManager);
      orchestration.setModuleManager(moduleManager);
      orchestration.setProblemGraph(graph);
      orchestration.setSolutionLength(1);
      orchestration.setFitnessFunction(createFitnessFunction(orchestration));

      final double[] ref1 = { -0.9, 1 };
      final double[] ref2 = { -1, 2 };
      final double[] ref3 = { -1.4, 3 };

      final NondominatedPopulation referenceSet = new NondominatedPopulation();
      referenceSet.add(new Solution(ref1));
      referenceSet.add(new Solution(ref2));
      referenceSet.add(new Solution(ref3));

      final Hypervolume h = new Hypervolume(orchestration.getProblem(), referenceSet);

      final NondominatedPopulation curNonDomPop = new NondominatedPopulation();

      final double[] s1 = { -0.9, 1 };
      final double[] s2 = { -1, 2 };
      final double[] s3 = { -1.4, 3 };

      curNonDomPop.add(new Solution(s1));
      curNonDomPop.add(new Solution(s2));
      curNonDomPop.add(new Solution(s3));

      System.out.println(h.evaluate(curNonDomPop));

   }

}
