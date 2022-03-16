package at.ac.tuwien.big.momot.examples.stack;

import at.ac.tuwien.big.moea.SearchAnalysis;
import at.ac.tuwien.big.moea.SearchExperiment;
import at.ac.tuwien.big.moea.SearchResultManager;
import at.ac.tuwien.big.moea.experiment.analyzer.SearchAnalyzer;
import at.ac.tuwien.big.moea.experiment.executor.SearchExecutor;
import at.ac.tuwien.big.moea.experiment.executor.listener.CurrentNondominatedPopulationPrintListener;
import at.ac.tuwien.big.moea.experiment.executor.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.moea.print.ISolutionWriter;
import at.ac.tuwien.big.moea.search.algorithm.EvolutionaryAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.RLAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.provider.IRegisteredAlgorithm;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.momot.TransformationResultManager;
import at.ac.tuwien.big.momot.TransformationSearchOrchestration;
import at.ac.tuwien.big.momot.examples.stack.stack.StackPackage;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.problem.solution.variable.ITransformationVariable;
import at.ac.tuwien.big.momot.search.algorithm.operator.mutation.TransformationParameterMutation;
import at.ac.tuwien.big.momot.search.algorithm.operator.mutation.TransformationPlaceholderMutation;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment.EnvironmentBuilder;
import at.ac.tuwien.big.momot.search.fitness.IEGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.util.MomotUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.TournamentSelection;

public class StackSearch {

   private static final int SOLUTION_LENGTH = 8;
   private static final String INPUT_MODEL = "model/model_five_stacks.xmi";

   protected static final boolean PRINT_POPULATIONS = false;
   protected static final String PRINT_DIRECTORY = "output/populations/five_stacks";

   private static final int NR_RUNS = 1;
   private static final int POPULATION_SIZE = 100;
   private static final int MAX_EVALUATIONS = 5000;

   private static String OUT_PATH;
   private static ISolutionWriter<TransformationSolution> SOLUTION_WRITER = null;

   protected static String baseName;

   protected static double significanceLevel = 0.01;

   private static IEGraphMultiDimensionalFitnessFunction FITNESS_FUNCTION;

   public static void evaluateSolution(final TransformationSolution s) {
      FITNESS_FUNCTION.doEvaluate(s);
   }

   protected static TransformationResultManager handleResults(
         final SearchExperiment<TransformationSolution> experiment) {

      final TransformationResultManager resultManager = new TransformationResultManager(experiment);

      System.out.println("REFERENCE SET:");
      System.out.println(SearchResultManager.printObjectives(SearchResultManager.getReferenceSet(experiment, null)));
      System.out.println(SearchResultManager.printObjectives(resultManager.createApproximationSet()));

      Population population;
      population = SearchResultManager.createApproximationSet(experiment, (String[]) null);
      System.out.println("- Save objectives of all algorithms to 'output/objectives/objective_values.txt'");
      SearchResultManager.saveObjectives("output/objectives/objective_values.txt", population);

      population = SearchResultManager.createApproximationSet(experiment, (String[]) null);
      System.out.println("- Save models of all algorithms to 'output/models/'");
      saveModels(TransformationResultManager.saveModels("output/models/", INPUT_MODEL, population));

      System.out.println("- Save objectives of algorithms seperately to 'output/objectives/<algorithm>.txt'");
      System.out.println("- Save models of algorithms seperately to 'output/solutions/<algorithm>.txt'´\n");

      for(final Entry<SearchExecutor, List<NondominatedPopulation>> entry : resultManager.getResults().entrySet()) {

         System.out.println(entry.getKey().getName());

         population = SearchResultManager.createApproximationSet(experiment, entry.getKey().getName());
         System.out.println(SearchResultManager.printObjectives(population) + "\n");
         saveModels(TransformationResultManager.saveModels("output/models/" + entry.getKey().getName(),
               entry.getKey().getName(), population));

         population = SearchResultManager.createApproximationSet(experiment, entry.getKey().getName());
         SearchResultManager.saveObjectives("output/objectives/" + entry.getKey().getName() + ".txt", population);
      }

      return resultManager;
   }

   public static void main(final String[] args) throws IOException {
      StackPackage.eINSTANCE.eClass();

      final StackOrchestration search = new StackOrchestration(INPUT_MODEL, SOLUTION_LENGTH);

      // Init builder
      final EnvironmentBuilder<TransformationSolution> envBuilder = new EnvironmentBuilder<>(
            search.getFitnessFunction());

      // Define utilities and build enviroments
      final Map<IEnvironment.Type, IEnvironment<TransformationSolution>> env = envBuilder
            .singleObjective("Standard Deviation").build();

      final RLAlgorithmFactory<TransformationSolution> rl = search.createRLAlgorithmFactory(env);

      final EvolutionaryAlgorithmFactory<TransformationSolution> moea = search
            .createEvolutionaryAlgorithmFactory(POPULATION_SIZE);

      search.addAlgorithm("NSGAII",
            moea.createNSGAII(new TournamentSelection(2), new OnePointCrossover(1.0),
                  new TransformationParameterMutation(0.1, search.getModuleManager()),
                  new TransformationPlaceholderMutation(0.2)));

      search.addAlgorithm("QLearningExplore",
            rl.createSingleObjectiveExploreQLearner(10, 0.9, 0.1, true, 1e-3, 0.1, null, 0, 0, null, null, false));

      search.addAlgorithm("QLearning",
            rl.createSingleObjectiveQLearner(0.9, 0.1, true, 1e-3, 0.1, null, 0, 0, null, null, false));

      final SearchExperiment<TransformationSolution> experiment = new SearchExperiment<>(search, MAX_EVALUATIONS);

      experiment.setNumberOfRuns(NR_RUNS);
      experiment.addProgressListener(new SeedRuntimePrintListener());

      if(PRINT_POPULATIONS) {
         final List<String> algoNames = new ArrayList<>();
         for(final IRegisteredAlgorithm<? extends Algorithm> a : search.getAlgorithms()) {
            algoNames.add(search.getAlgorithmName(a));
         }

         if(!Files.exists(Paths.get(PRINT_DIRECTORY))) {
            new File(PRINT_DIRECTORY).mkdirs();
         }

         // experiment.addProgressListener(
         // new CurrentNondominatedPopulationPrintListener(PRINT_DIRECTORY, algoNames, NR_RUNS, printInterval));
      }

      printSearchInfo(search);

      experiment.run();

      System.out.println("-------------------------------------------------------");
      System.out.println("Analysis");
      System.out.println("-------------------------------------------------------");
      performAnalysis(experiment);
      System.out.println("-------------------------------------------------------");
      System.out.println("Results");
      System.out.println("-------------------------------------------------------");
      handleResults(experiment);
   }

   public static TransformationSolution newSolutionFromVariables(final Resource r,
         final List<ITransformationVariable> vars) {
      final TransformationSolution ts = new TransformationSolution(MomotUtil.eGraphOf(r, true), vars, 2);
      ts.execute();
      StackSearch.evaluateSolution(ts);
      return ts;
   }

   protected static SearchAnalyzer performAnalysis(final SearchExperiment<TransformationSolution> experiment) {
      final SearchAnalysis analysis = new SearchAnalysis(experiment);
      analysis.setHypervolume(true);
      analysis.setShowAggregate(true);
      analysis.setShowIndividualValues(true);
      analysis.setShowStatisticalSignificance(true);
      analysis.setSignificanceLevel(significanceLevel);
      final SearchAnalyzer searchAnalyzer = analysis.analyze();
      System.out.println("---------------------------");
      System.out.println("Analysis Results");
      System.out.println("---------------------------");
      searchAnalyzer.printAnalysis();
      System.out.println("---------------------------");
      try {
         System.out.println("- Save Analysis to 'output/analysis/analysis.txt'");
         searchAnalyzer.saveAnalysis(new File("output/analysis/analysis.txt"));
      } catch(final IOException e) {
         e.printStackTrace();
      }
      System.out.println("- Save Indicator BoxPlots to 'output/analysis/'");
      searchAnalyzer.saveIndicatorBoxPlots("output/analysis/", baseName);
      return searchAnalyzer;
   }

   public static void printSearchInfo(final TransformationSearchOrchestration orchestration) {
      final List<String> moduleNames = new ArrayList<>();
      for(final org.eclipse.emf.henshin.model.Module m : orchestration.getModuleManager().getModules()) {
         moduleNames.add(m.getName());
      }
      ;
      System.out.println("-------------------------------------------------------");
      System.out.println("Search");
      System.out.println("-------------------------------------------------------");
      // System.out.println("InputModel: " + INITIAL_MODEL);
      System.out.println("Objectives:      " + orchestration.getFitnessFunction().getObjectiveNames());
      System.out.println("NrObjectives:    " + orchestration.getNumberOfObjectives());
      System.out.println("Constraints:     " + orchestration.getFitnessFunction().getConstraintNames());
      System.out.println("NrConstraints:   " + orchestration.getNumberOfConstraints());
      System.out.println("Transformations: " + moduleNames.toString());
      System.out.println("Units:           " + orchestration.getModuleManager().getUnits());
      System.out.println("SolutionLength:  " + orchestration.getSolutionLength());
      System.out.println("PopulationSize:  " + POPULATION_SIZE);
      System.out.println("Iterations:      " + MAX_EVALUATIONS / POPULATION_SIZE);
      System.out.println("MaxEvaluations:  " + MAX_EVALUATIONS);
      System.out.println("AlgorithmRuns:   " + NR_RUNS);
      System.out.println("---------------------------");
   }

   public static void saveModel(final String outPath, final TransformationSolution s) {
      TransformationResultManager.saveModel(Paths.get(OUT_PATH, outPath).toString(), s);
   }

   public static void saveModels(final List<File> modelFiles) {
      for(final File file : modelFiles) {
         final EGraph graph = MomotUtil.loadGraph(file.getPath());
         MomotUtil.saveGraph(graph, file.getPath());
      }
   }

   public static void saveSolution(final PrintStream ps, final TransformationSolution solution) {
      SearchResultManager.saveSolution(ps, solution, SOLUTION_WRITER, false);

   }

   public static void saveSolution(final String filePath, final TransformationSolution s,
         ISolutionWriter<TransformationSolution> solutionWriter) {
      if(solutionWriter == null) {
         solutionWriter = SOLUTION_WRITER;
      }
      SearchResultManager.saveSolution(Paths.get(OUT_PATH, filePath).toString(), s, solutionWriter);
   }

   public StackSearch(final String outBasePath) {
      OUT_PATH = outBasePath;
   }

   protected SearchExperiment<TransformationSolution> createExperiment(
         final TransformationSearchOrchestration orchestration) {
      final SearchExperiment<TransformationSolution> experiment = new SearchExperiment<>(orchestration,
            MAX_EVALUATIONS);
      experiment.setNumberOfRuns(NR_RUNS);
      experiment.addProgressListener(new SeedRuntimePrintListener());

      if(PRINT_POPULATIONS) {
         final List<String> algoNames = new ArrayList<>();
         for(final IRegisteredAlgorithm<? extends Algorithm> a : orchestration.getAlgorithms()) {
            algoNames.add(orchestration.getAlgorithmName(a));
         }

         if(!Files.exists(Paths.get(PRINT_DIRECTORY))) {
            new File(PRINT_DIRECTORY).mkdirs();
         }

         experiment.addProgressListener(
               new CurrentNondominatedPopulationPrintListener(PRINT_DIRECTORY, algoNames, NR_RUNS, 100));
      }

      return experiment;
   }

   protected TransformationSearchOrchestration createOrchestration(final String initialGraph,
         final int solutionLength) {
      StackPackage.eINSTANCE.eClass();

      final StackOrchestration orchestration = new StackOrchestration(initialGraph, solutionLength);

      final EvolutionaryAlgorithmFactory<TransformationSolution> moea = orchestration
            .createEvolutionaryAlgorithmFactory(POPULATION_SIZE);

      orchestration.addAlgorithm("NSGAII",
            moea.createNSGAII(new TournamentSelection(2), new OnePointCrossover(1.0),
                  new TransformationParameterMutation(0.1, orchestration.getModuleManager()),
                  new TransformationPlaceholderMutation(0.2)));

      return orchestration;
   }

   protected TransformationSearchOrchestration createOrchestration(final String initialGraph, final int solutionLength,
         final String algorithmName) {
      StackPackage.eINSTANCE.eClass();

      final StackOrchestration orchestration = new StackOrchestration(initialGraph, solutionLength);

      final EvolutionaryAlgorithmFactory<TransformationSolution> moea = orchestration
            .createEvolutionaryAlgorithmFactory(POPULATION_SIZE);

      final EnvironmentBuilder<TransformationSolution> envBuilder = new EnvironmentBuilder<>(
            orchestration.getFitnessFunction());
      final Map<IEnvironment.Type, IEnvironment<TransformationSolution>> env = envBuilder
            .singleObjective("Standard Deviation").build();

      final RLAlgorithmFactory<TransformationSolution> rl = orchestration.createRLAlgorithmFactory(env);

      if(algorithmName.compareTo("NSGAII") == 0) {
         orchestration.addAlgorithm("NSGAII",
               moea.createNSGAII(new TournamentSelection(2), new OnePointCrossover(1.0),
                     new TransformationParameterMutation(0.1, orchestration.getModuleManager()),
                     new TransformationPlaceholderMutation(0.2)));
      }

      if(algorithmName.compareTo("QLearning") == 0) {
         orchestration.addAlgorithm("QLearning",
               rl.createSingleObjectiveQLearner(0.9, 0.3, false, 1e-3, 0.1, null, 0, 0, null, null, false));
      }

      if(algorithmName.compareTo("QLearningExplore") == 0) {
         orchestration.addAlgorithm("QLearningExplore",
               rl.createSingleObjectiveExploreQLearner(10, 0.9, 0.3, false, 1e-3, 0.1, null, 0, 0, null, null, false));
      }

      return orchestration;
   }

   protected void deriveBaseName(final TransformationSearchOrchestration orchestration) {
      final EObject root = MomotUtil.getRoot(orchestration.getProblemGraph());
      if(root == null || root.eResource() == null || root.eResource().getURI() == null) {
         baseName = getClass().getSimpleName();
      } else {
         baseName = root.eResource().getURI().trimFileExtension().lastSegment();
      }
   }

   private List<ITransformationVariable> getOptimalSolutionForAlgorithm(
         final SearchExperiment<TransformationSolution> experiment,
         final TransformationSearchOrchestration orchestration, final String algorithmName, final String dir,
         final String solutionFilename, final String modelFilename) {

      final Map<String, List<ITransformationVariable>> algorithmToBestRuleSequence = new HashMap<>();
      for(final Entry<SearchExecutor, List<NondominatedPopulation>> entry : experiment.getResults().entrySet()) {
         if(entry.getKey().getName().compareTo(algorithmName) != 0) {
            continue;
         }

         double minStd = Double.POSITIVE_INFINITY;
         TransformationSolution optimalTS = null;
         final Population population = SearchResultManager.createApproximationSet(experiment, algorithmName);
         for(final TransformationSolution ts : MomotUtil.asIterables(population, TransformationSolution.class)) {
            final double curStd = ts
                  .getObjective(orchestration.getFitnessFunction().getObjectiveIndex("Standard Deviation"));
            if(curStd < minStd) {
               minStd = curStd;
               optimalTS = TransformationSolution.removePlaceholders(ts);
               ts.execute();
            }
         }
         // algorithmToBestRuleSequence.put(algName, optimalTS.getVariablesAsList());
         evaluateSolution(optimalTS);
         StackSearch.saveSolution(Paths.get(dir, algorithmName + "_plan_" + solutionFilename + ".txt").toString(),
               optimalTS, SOLUTION_WRITER);
         StackSearch.saveModel(Paths.get(dir, algorithmName + "_model_" + modelFilename + ".xmi").toString(),
               optimalTS);
         return optimalTS.getVariablesAsList();

      }
      return null;
   }

   public List<ITransformationVariable> performInitialSearch(final String initialGraph, final int solutionLength,
         final String algorithmName) {
      final TransformationSearchOrchestration orchestration = createOrchestration(initialGraph, solutionLength,
            algorithmName);
      deriveBaseName(orchestration);
      // printSearchInfo(orchestration);
      final SearchExperiment<TransformationSolution> experiment = createExperiment(orchestration);
      experiment.run();

      SOLUTION_WRITER = experiment.getSearchOrchestration().createSolutionWriter();
      FITNESS_FUNCTION = orchestration.getFitnessFunction();

      return getOptimalSolutionForAlgorithm(experiment, orchestration, algorithmName, "initial", "initial",
            "after_initial_plan");

   }

   public List<ITransformationVariable> performReplanningSearch(final String initialGraph, final int solutionLength,
         final String algorithmName, final String runName) {
      final TransformationSearchOrchestration orchestration = createOrchestration(initialGraph, solutionLength,
            algorithmName);
      deriveBaseName(orchestration);
      // printSearchInfo(orchestration);
      final SearchExperiment<TransformationSolution> experiment = createExperiment(orchestration);
      experiment.run();

      return getOptimalSolutionForAlgorithm(experiment, orchestration, algorithmName, "replan_" + runName,
            "replan_" + runName, "after_replan_" + runName);
   }
}
