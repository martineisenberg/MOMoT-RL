package PacmanGame.search;

import at.ac.tuwien.big.moea.SearchExperiment;
import at.ac.tuwien.big.moea.experiment.executor.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.moea.search.algorithm.LocalSearchAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.RLAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension;
import at.ac.tuwien.big.momot.ModuleManager;
import at.ac.tuwien.big.momot.TransformationSearchOrchestration;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.datastructures.RewardFactory;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain.EncodingFactory;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain.strategies.PacmanEncodingStrategy;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain.strategies.PacmanRewardStrategy;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment.EnvironmentBuilder;
import at.ac.tuwien.big.momot.search.fitness.EGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.IEGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.dimension.AbstractEGraphFitnessDimension;
import at.ac.tuwien.big.momot.search.fitness.dimension.TransformationLengthDimension;
import at.ac.tuwien.big.momot.util.MomotUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.moeaframework.util.progress.ProgressListener;

import PacmanGame.PacmanGamePackage;
import PacmanGame.impl.GameImpl;

@SuppressWarnings("all")
public class PacmanSearchPolicyGradient {
   protected static final String INITIAL_MODEL = "models/input_8x8.xmi";

   protected static final int SOLUTION_LENGTH = 20;

   public static void finalization() {
      System.out.println("Search finished.");
   }

   public static void initialization() {
      PacmanGamePackage.eINSTANCE.eClass();

      System.out.println("Search started.");
   }

   public static void main(final String... args) {
      initialization();
      final PacmanSearchPolicyGradient search = new PacmanSearchPolicyGradient();
      search.performSearch(INITIAL_MODEL, SOLUTION_LENGTH);
      finalization();
   }

   protected final String[] modules = new String[] { "transformation/kill.henshin", "transformation/eat.henshin",
         "transformation/move_up.henshin", "transformation/move_down.henshin", "transformation/move_left.henshin",
         "transformation/move_right.henshin" };

   protected final int MAX_EVALUATIONS = 30000000;

   protected final int NR_RUNS = 1;

   protected String baseName;

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

   protected IFitnessDimension<TransformationSolution> _createObjective_5(
         final TransformationSearchOrchestration orchestration) {
      return new AbstractEGraphFitnessDimension("Score",
            at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Maximum) {
         @Override
         protected double internalEvaluate(final TransformationSolution solution) {
            final EGraph graph = solution.execute();
            final EObject root = MomotUtil.getRoot(graph);
            return createObjectiveHelperScore(solution, graph, root);
         }
      };
   }

   protected IFitnessDimension<TransformationSolution> _createObjectiveHelper_0() {
      final TransformationLengthDimension _transformationLengthDimension = new TransformationLengthDimension();
      return _transformationLengthDimension;
   }

   protected void adaptResultModel(final EObject root) {
   }

   protected void adaptResultModels(final List<File> modelFiles) {
      final HenshinResourceSet set = new HenshinResourceSet();
      for(final File file : modelFiles) {
         final EGraph graph = MomotUtil.loadGraph(file.getPath());
         final EObject root = MomotUtil.getRoot(graph);
         adaptResultModel(root);
         MomotUtil.saveGraph(graph, file.getPath());
      }
   }

   protected SearchExperiment<TransformationSolution> createExperiment(
         final TransformationSearchOrchestration orchestration) {
      final SearchExperiment<TransformationSolution> experiment = new SearchExperiment<>(orchestration,
            MAX_EVALUATIONS);
      experiment.setNumberOfRuns(NR_RUNS);
      experiment.addProgressListener(_createListener_0());
      return experiment;
   }

   protected IEGraphMultiDimensionalFitnessFunction createFitnessFunction(
         final TransformationSearchOrchestration orchestration) {
      final IEGraphMultiDimensionalFitnessFunction function = new EGraphMultiDimensionalFitnessFunction();
      function.addObjective(_createObjective_5(orchestration));
      function.addObjective(_createObjective_0(orchestration));

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
      return manager;
   }

   protected double createObjectiveHelperScore(final TransformationSolution solution, final EGraph graph,
         final EObject root) {

      final GameImpl resultGame = (GameImpl) MomotUtil.getRoot(graph);

      return resultGame.getScoreboard().getScore();
   }

   protected TransformationSearchOrchestration createOrchestration(final String initialGraph,
         final int solutionLength) {

      final TransformationSearchOrchestration orchestration = new TransformationSearchOrchestration();
      final ModuleManager moduleManager = createModuleManager();
      final EGraph graph = createInputGraph(initialGraph, moduleManager);
      orchestration.setModuleManager(moduleManager);
      orchestration.setProblemGraph(graph);
      orchestration.setSolutionLength(solutionLength);
      final IEGraphMultiDimensionalFitnessFunction fitnessFunction = createFitnessFunction(orchestration);
      orchestration.setFitnessFunction(fitnessFunction);

      final EnvironmentBuilder<TransformationSolution> envBuilder = new EnvironmentBuilder<>(fitnessFunction);

      final Map<IEnvironment.Type, IEnvironment<TransformationSolution>> env = envBuilder
            .encodingStrategy(EncodingFactory.createEncoder(PacmanEncodingStrategy.class))
            .rewardStrategy(RewardFactory.createRewardStrategy(PacmanRewardStrategy.class)).singleObjective("Score")
            .build();

      final LocalSearchAlgorithmFactory<TransformationSolution> local = orchestration
            .createLocalSearchAlgorithmFactory();

      final RLAlgorithmFactory<TransformationSolution> rl = orchestration.createRLAlgorithmFactory(env);

      orchestration.addAlgorithm("PG",
            rl.createPolicyGradient(0.95, 1e-4, false, 0, 0, null, false, null, "PG_4x4", 100, false, 10000, true));

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

   public void performSearch(final String initialGraph, final int solutionLength) {
      final TransformationSearchOrchestration orchestration = createOrchestration(initialGraph, solutionLength);
      deriveBaseName(orchestration);
      printSearchInfo(orchestration);
      final SearchExperiment<TransformationSolution> experiment = createExperiment(orchestration);
      experiment.run();
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
      System.out.println("MaxEvaluations:  " + MAX_EVALUATIONS);
      System.out.println("AlgorithmRuns:   " + NR_RUNS);
      System.out.println("---------------------------");
   }
}
