package container.demo;

import at.ac.tuwien.big.moea.SearchAnalysis;
import at.ac.tuwien.big.moea.SearchExperiment;
import at.ac.tuwien.big.moea.SearchResultManager;
import at.ac.tuwien.big.moea.experiment.analyzer.SearchAnalyzer;
import at.ac.tuwien.big.moea.experiment.executor.SearchExecutor;
import at.ac.tuwien.big.moea.experiment.executor.listener.CurrentNondominatedPopulationPrintListener;
import at.ac.tuwien.big.moea.experiment.executor.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.moea.print.IPopulationWriter;
import at.ac.tuwien.big.moea.print.ISolutionWriter;
import at.ac.tuwien.big.moea.search.algorithm.EvolutionaryAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.LocalSearchAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.RLAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.provider.IRegisteredAlgorithm;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension;
import at.ac.tuwien.big.momot.ModuleManager;
import at.ac.tuwien.big.momot.TransformationResultManager;
import at.ac.tuwien.big.momot.TransformationSearchOrchestration;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.datastructures.RewardFactory;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain.EncodingFactory;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain.strategies.ContainerEncodingStrategy;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain.strategies.ContainerRewardStrategy;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment.EnvironmentBuilder;
import at.ac.tuwien.big.momot.search.fitness.EGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.IEGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.dimension.AbstractEGraphFitnessDimension;
import at.ac.tuwien.big.momot.search.fitness.dimension.TransformationLengthDimension;
import at.ac.tuwien.big.momot.util.MomotUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.util.progress.ProgressListener;

import container.ContainerModel;
import container.ContainerPackage;

@SuppressWarnings("all")
public class ContainerSearchTestNN {

   protected static String testModelPath = "rand_gen_models/test_10_3S8C";
   protected static String toEvalModelPath = "output/rl/nn/ac/Actor";

   protected static final boolean PRINT_POPULATIONS = true;
   protected static final String PRINT_DIRECTORY = "output/populations/AC_3S8C";

   private static String INITIAL_MODEL;
   protected static final int SOLUTION_LENGTH = 20;

   public static void finalization() {
      System.out.println("Search finished.");
   }

   public static void initialization() {
      ContainerPackage.eINSTANCE.eClass();
      System.out.println("Search started.");
   }

   // public static void main(final String... args) {
   // initialization();
   // final ContainerSearch search = new ContainerSearch();
   // search.performSearch(INITIAL_MODEL, SOLUTION_LENGTH);
   // finalization();
   // }

   public static void main(final String... args) {
      initialization();

      final File[] testModelFiles = new File(testModelPath).listFiles();

      final List<File> trainedModelFileList = Arrays.stream(new File(toEvalModelPath).listFiles())
            .collect(Collectors.toList());

      Arrays.sort(testModelFiles);
      trainedModelFileList.add(null);

      // Store console print stream.
      final PrintStream ps_console = System.out;

      final File file = new File("out_ac_traintest.txt");
      FileOutputStream fos;
      try {
         fos = new FileOutputStream(file);
         final PrintStream ps = new PrintStream(fos);
         System.setOut(ps);

      } catch(final FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      trainedModelFileList.stream().filter(f -> f == null || !f.isDirectory()).forEach(nnFile -> {
         Stream.of(testModelFiles).filter(f -> !f.isDirectory()).forEach(testModelFile -> {

            INITIAL_MODEL = testModelFile.getName();
            final ContainerSearchTestNN search = new ContainerSearchTestNN();
            search.performSearch(testModelFile.getAbsolutePath(), nnFile != null ? nnFile.getAbsolutePath() : null,
                  SOLUTION_LENGTH);
         });
      });

   }

   protected final String[] modules = new String[] { "transformations/container.henshin" };

   protected final String[] unitsToRemove = new String[] { "container::containerModule::retrieveNonLastFromStack",
         "container::containerModule::canRetrieveContainer", "container::containerModule::checkNextToRetrieveIsLast",
         "container::containerModule::relocateNonLastOnStackToEmptyStack",
         "container::containerModule::relocateNonLastOnStackToNonEmptyStack",
         "container::containerModule::relocateLastOnStackToNonEmptyStack", "container::containerModule::checkIsLast",
         "container::containerModule::checkIsNotLast", "container::containerModule::checkTargetStackEmpty",
         "container::containerModule::RelocateLastOnStack", "container::containerModule::checkTargetStackNotEmpty",
         "container::containerModule::Relocate", "container::containerModule::Retrieve",
         "container::containerModule::RelocateNonLastOnStack", "container::containerModule::retrieveLastFromStack",
         "container::containerModule::checkContainerToRetrieveOnTopOfSuccessor",
         "container::containerModule::checkContainerToRetrieveLastOverall",
         "container::containerModule::retrieveOnTopOfSuccessorFromStack",
         "container::containerModule::retrieveLastOverallFromStack",
         "container::containerModule::RetrieveNormalLastOrNonLastOnStack",
         "container::containerModule::RetrieveNonLastOverall",
         "container::containerModule::RelocateLastOnStackToNonEmptyTargetStack",
         "container::containerModule::RelocateNonLastOnStackToEmptyTargetStack",
         "container::containerModule::RelocateNonLastOnStackToNonEmptyTargetStack" };

   protected final int populationSize = 100;

   protected final int maxEvaluations = 10000;

   protected final int nrRuns = 1;

   protected String baseName;

   protected double significanceLevel = 0.01;

   protected ProgressListener _createListener_0() {
      final SeedRuntimePrintListener _seedRuntimePrintListener = new SeedRuntimePrintListener();
      return _seedRuntimePrintListener;
   }

   protected IFitnessDimension<TransformationSolution> _createObjective_0(
         final TransformationSearchOrchestration orchestration) {
      final IFitnessDimension<TransformationSolution> dimension = _createObjectiveHelper_0();
      dimension.setName("SolutionLength");
      dimension.setFunctionType(at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Minimum);
      return dimension;
   }

   protected IFitnessDimension<TransformationSolution> _createObjective_1(
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

   protected IFitnessDimension<TransformationSolution> _createObjective_2(
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

   protected IFitnessDimension<TransformationSolution> _createObjectiveHelper_0() {
      final TransformationLengthDimension _transformationLengthDimension = new TransformationLengthDimension();
      return _transformationLengthDimension;
   }

   protected double _createObjectiveHelper_1(final TransformationSolution solution, final EGraph graph,
         final EObject root) {
      return ContainerUtils.calculateRetrievedContainers((ContainerModel) root);
   }

   protected double _createObjectiveHelper_2(final TransformationSolution solution, final EGraph graph,
         final EObject root) {
      return ContainerUtils.noRetrievedContainers((ContainerModel) root);
   }

   protected IRegisteredAlgorithm<NSGAII> _createRegisteredAlgorithm_0(
         final TransformationSearchOrchestration orchestration,
         final EvolutionaryAlgorithmFactory<TransformationSolution> moea,
         final LocalSearchAlgorithmFactory<TransformationSolution> local) {
      final IRegisteredAlgorithm<NSGAII> _createNSGAII = moea.createNSGAII();
      return _createNSGAII;
   }

   protected SearchExperiment<TransformationSolution> createExperiment(
         final TransformationSearchOrchestration orchestration) {
      final SearchExperiment<TransformationSolution> experiment = new SearchExperiment<>(orchestration, maxEvaluations);
      experiment.setNumberOfRuns(nrRuns);
      experiment.addProgressListener(_createListener_0());

      if(PRINT_POPULATIONS) {
         final List<String> algorithmNames = new ArrayList<>();
         for(final IRegisteredAlgorithm<? extends Algorithm> a : orchestration.getAlgorithms()) {
            algorithmNames.add(orchestration.getAlgorithmName(a));
         }

         if(!Files.exists(Paths.get(PRINT_DIRECTORY))) {
            new File(PRINT_DIRECTORY).mkdirs();
         }

         experiment.addProgressListener(
               new CurrentNondominatedPopulationPrintListener(PRINT_DIRECTORY, algorithmNames, nrRuns, 100));

      }

      return experiment;
   }

   protected IEGraphMultiDimensionalFitnessFunction createFitnessFunction(
         final TransformationSearchOrchestration orchestration) {
      final IEGraphMultiDimensionalFitnessFunction function = new EGraphMultiDimensionalFitnessFunction();
      function.addObjective(_createObjective_0(orchestration));
      function.addObjective(_createObjective_1(orchestration));
      // function.addObjective(_createObjective_2(orchestration));
      return function;
   }

   protected EGraph createInputGraph(final String initialGraph, final ModuleManager moduleManager) {
      final EGraph graph = moduleManager.loadGraph(initialGraph);
      return graph;
   }

   protected ModuleManager createModuleManager() {
      final ModuleManager manager = new ModuleManager();
      for(final String module : modules) {
         manager.addModule(URI.createFileURI(new File(module).getPath().toString()).toString());
      }
      manager.removeUnits(unitsToRemove);
      return manager;
   }

   protected TransformationSearchOrchestration createOrchestration(final String initialGraph, final String nnFile,
         final int solutionLength) {
      final TransformationSearchOrchestration orchestration = new TransformationSearchOrchestration();
      final ModuleManager moduleManager = createModuleManager();
      final EGraph graph = createInputGraph(initialGraph, moduleManager);
      orchestration.setModuleManager(moduleManager);
      orchestration.setProblemGraph(graph);
      orchestration.setSolutionLength(solutionLength);
      final IEGraphMultiDimensionalFitnessFunction fitnessFunction = createFitnessFunction(orchestration);
      orchestration.setFitnessFunction(fitnessFunction);

      // final IEnvironment<TransformationSolution> env = new GenericEnvironment<>(
      // new ObjectiveFitnessComparator<TransformationSolution>(
      // orchestration.getFitnessFunction().getObjectiveIndex("RetrievedContainers")));

      final EnvironmentBuilder<TransformationSolution> envBuilder = new EnvironmentBuilder<>(fitnessFunction);

      final Map<IEnvironment.Type, IEnvironment<TransformationSolution>> env = envBuilder
            .encodingStrategy(EncodingFactory.createEncoder(ContainerEncodingStrategy.class))
            .rewardStrategy(RewardFactory.createRewardStrategy(ContainerRewardStrategy.class))
            .singleObjective("RetrievedContainers").build();
      // final IEnvironment<TransformationSolution> env = new DomainEnvironment<>(
      // new SolutionProvider<>(orchestration.getSearchHelper()),
      // new ObjectiveFitnessComparator<TransformationSolution>(
      // orchestration.getFitnessFunction().getObjectiveIndex("RetrievedContainers")),
      // EncodingFactory.createEncoder(ContainerEncoding.class));

      final RLAlgorithmFactory<TransformationSolution> rl = orchestration.createRLAlgorithmFactory(env);

      final EvolutionaryAlgorithmFactory<TransformationSolution> moea = orchestration
            .createEvolutionaryAlgorithmFactory(populationSize);
      final LocalSearchAlgorithmFactory<TransformationSolution> local = orchestration
            .createLocalSearchAlgorithmFactory();

      // ###### TEST
      if(nnFile == null) {
         // orchestration.addAlgorithm("PGplain", rl.createPolicyGradient(0.95, 5e-5, false, 0, 0, null, false, null,
         // "test3S8C/PGplain_", 100, false, 0, false));
         orchestration.addAlgorithm("ACplain", rl.createActorCritic(0.95, 5e-5, 0, 0, null, null, false, null, null,
               "test3S8C/ACp_", 100, false, 0, false));
      } else {
         // orchestration.addAlgorithm("PG" + FilenameUtils.getBaseName(nnFile), rl.createPolicyGradient(0.95, 5e-5,
         // false,
         // 0, 0, nnFile, false, null, "test3S8C/PG_" + FilenameUtils.getBaseName(nnFile), 100, false, 0, false));
         orchestration.addAlgorithm("AC" + FilenameUtils.getBaseName(nnFile),
               rl.createActorCritic(0.95, 5e-5, 0, 0, nnFile,
                     nnFile.replaceFirst("Actor\\\\A_train_", "Critic\\\\C_train_"), false, null, null,
                     "test3S8C/AC_" + FilenameUtils.getBaseName(nnFile), 100, false, 0, false));
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

   protected TransformationResultManager handleResults(final SearchExperiment<TransformationSolution> experiment) {
      final ISolutionWriter<TransformationSolution> solutionWriter = experiment.getSearchOrchestration()
            .createSolutionWriter();
      final IPopulationWriter<TransformationSolution> populationWriter = experiment.getSearchOrchestration()
            .createPopulationWriter();
      final TransformationResultManager resultManager = new TransformationResultManager(experiment);
      Population population;
      population = SearchResultManager.createApproximationSet(experiment, (String[]) null);
      System.out.println("- Save objectives of all algorithms to 'output/objectives/objective_values.txt'");
      SearchResultManager.saveObjectives("output/objectives/objective_values.txt", population);
      System.out.println("---------------------------");
      System.out.println("Objectives of all algorithms");
      System.out.println("---------------------------");
      System.out.println(SearchResultManager.printObjectives(population));

      population = SearchResultManager.createApproximationSet(experiment, (String[]) null);
      System.out.println("- Save solutions of all algorithms to 'output/solutions/objective_values.txt'");
      SearchResultManager.savePopulation("output/solutions/objective_values.txt", population, populationWriter);
      System.out.println("- Save solutions of all algorithms to 'output/solutions/objective_values.txt'");
      SearchResultManager.saveSolutions("output/solutions/", baseName,
            MomotUtil.asIterables(population, TransformationSolution.class), solutionWriter);

      population = SearchResultManager.createApproximationSet(experiment, (String[]) null);
      System.out.println("- Save models of all algorithms to 'output/models/'");
      TransformationResultManager.saveModels("output/models/", baseName, population);

      for(final Entry<SearchExecutor, List<NondominatedPopulation>> entry : resultManager.getResults().entrySet()) {

         System.out.println(entry.getKey().getName());

         population = SearchResultManager.createApproximationSet(experiment, entry.getKey().getName());
         System.out.println(SearchResultManager.printObjectives(population) + "\n");

      }

      return resultManager;
   }

   protected SearchAnalyzer performAnalysis(final SearchExperiment<TransformationSolution> experiment) {
      final SearchAnalysis analysis = new SearchAnalysis(experiment);
      analysis.setHypervolume(true);
      analysis.setGenerationalDistance(true);
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

   public void performSearch(final String initialGraph, final String nnFile, final int solutionLength) {
      final TransformationSearchOrchestration orchestration = createOrchestration(initialGraph, nnFile, solutionLength);
      deriveBaseName(orchestration);
      printSearchInfo(orchestration);
      final SearchExperiment<TransformationSolution> experiment = createExperiment(orchestration);
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

   public void printSearchInfo(final TransformationSearchOrchestration orchestration) {
      System.out.println("-------------------------------------------------------");
      System.out.println("Search");
      System.out.println("-------------------------------------------------------");
      System.out.println("InputModel:      " + INITIAL_MODEL);
      System.out.println("Objectives:      " + orchestration.getFitnessFunction().getObjectiveNames());
      System.out.println("NrObjectives:    " + orchestration.getNumberOfObjectives());
      System.out.println("Constraints:     " + orchestration.getFitnessFunction().getConstraintNames());
      System.out.println("NrConstraints:   " + orchestration.getNumberOfConstraints());
      System.out.println("Transformations: " + Arrays.toString(modules));
      System.out.println("Units:           " + orchestration.getModuleManager().getUnits());
      System.out.println("SolutionLength:  " + orchestration.getSolutionLength());
      System.out.println("PopulationSize:  " + populationSize);
      System.out.println("Iterations:      " + maxEvaluations / populationSize);
      System.out.println("MaxEvaluations:  " + maxEvaluations);
      System.out.println("AlgorithmRuns:   " + nrRuns);
      System.out.println("---------------------------");
   }
}
