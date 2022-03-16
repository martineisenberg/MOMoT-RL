package at.ac.tuwien.big.moea.search.algorithm.reinforcement.algorithms;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.AbstractMOTabularRLAgent;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.IParetoQTableAccessor;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ParetoQState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.DoneStatus;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IMOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.MOEnvResponse;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.EvaluationStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.LocalSearchStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class ParetoQLearning<S extends Solution> extends AbstractMOTabularRLAgent<S> {

   private final IParetoQTableAccessor<List<ApplicationState>, List<ApplicationState>> qTable;
   private final Map<List<ApplicationState>, NondominatedPopulation> qStateNDP;

   private final double gamma; // Eagerness - 0 looks in the near future, 1 looks in the distant future
   private final EvaluationStrategy strategy; // Eagerness - 0 looks in the near future, 1 looks in the distant future

   private double eps;
   private final double epsDecay;
   private final double epsMinimum;
   private final boolean withEpsDecay;
   private final int exploreSteps;
   private final LocalSearchStrategy localSearchStrategy;

   public ParetoQLearning(final LocalSearchStrategy localSearchStrategy, final int exploreSteps, final double gamma,
         final EvaluationStrategy strategy, final double eps, final boolean withEpsDecay, final double epsDecay,
         final double epsMinimum, final Problem problem, final IMOEnvironment<S> environment, final String savePath,
         final int recordInterval, final int terminateAfterEpisodes, final String qTableIn, final String qTableOut,
         final boolean verbose) {
      super(problem, environment, savePath, recordInterval, terminateAfterEpisodes, qTableIn, qTableOut, verbose);

      this.qStateNDP = new HashMap<>();
      this.gamma = gamma;
      this.eps = eps;
      this.epsDecay = epsDecay;
      this.epsMinimum = epsMinimum;
      this.withEpsDecay = withEpsDecay;
      this.strategy = strategy;
      startTime = System.currentTimeMillis();
      this.exploreSteps = exploreSteps;
      this.localSearchStrategy = localSearchStrategy;

      if(this.qTableIn != null) {
         this.qTable = this.utils.loadParetoQTable(qTableIn, environment.getUnitMapping());
      } else {
         this.qTable = this.utils.initParetoQTable(environment.getUnitMapping());
         this.qTable.addStateIfNotExists(new ArrayList<>());
         this.qStateNDP.put(new ArrayList<>(), new NondominatedPopulation());

      }
   }

   @Override
   public List<ApplicationState> epsGreedyDecision() {

      List<ApplicationState> nextAction = null;

      // Strategies
      if(rng.nextDouble() >= this.eps) {
         final NondominatedPopulation stateQNDP = qStateNDP.get(utils.getApplicationStates(currentSolution));

         if(stateQNDP.isEmpty()) {
            return null;
         } else if(stateQNDP.size() == 1) {
            qTable.getActionForCurrentState(stateQNDP, this.utils.getApplicationStates(currentSolution));

         }

         nextAction = this.qTable.getMaxRewardAction(this.strategy, utils.getApplicationStates(currentSolution),
               this.problem, stateQNDP);

      } else if(withEpsDecay && eps >= epsMinimum)

      { // nextAction = null => explore and decrease eps if above threshold
         eps -= epsDecay;
      }

      return nextAction;
   }

   @Override
   protected void iterate() {
      if(this.startTime == 0) {
         this.startTime = System.currentTimeMillis();
      }

      epochSteps++;
      final List<ApplicationState> nextAction = epsGreedyDecision();

      MOEnvResponse<S> response = null;

      response = (MOEnvResponse<S>) environment.step(localSearchStrategy, nextAction, exploreSteps);

      final double[] rewards = response.getRewards();
      final S nextState = response.getState();
      final DoneStatus doneStatus = response.getDoneStatus();

      if(doneStatus != DoneStatus.FINAL_STATE_REACHED) {
         updateQSets(utils.getApplicationStates(currentSolution),
               utils.getApplicationStatesDiff(currentSolution, nextState), utils.getApplicationStates(nextState),
               rewards);

         addSolutionIfImprovement(nextState);

         iterations++;

         printIfVerboseMode("Iteration: " + iterations);

      }

      if(doneStatus == DoneStatus.MAX_LENGTH_REACHED || doneStatus == DoneStatus.FINAL_STATE_REACHED) {

         if(this.saveInterval > 0) {

            framesList.add((double) iterations);
            timePassedList.add((double) (System.currentTimeMillis() - startTime));

            if(scoreSavePath != null && nrOfEpochs > 0 && nrOfEpochs % this.saveInterval == 0) {
               saveRewards(scoreSavePath, framesList, this.environment.getFunctionNames(), rewardEarnedLists,
                     timePassedList, meanRewardEarnedLists);
            }

         }

         nrOfEpochs++;
         epochSteps = 0;
         currentSolution = environment.reset();
      } else {
         currentSolution = nextState;
      }

   }

   private void updateQSets(final List<ApplicationState> state, final List<ApplicationState> action,
         final List<ApplicationState> nextState, final double[] rewards) {

      final NondominatedPopulation sPool = qStateNDP.get(state);
      ParetoQState pQState = qTable.getParetoQState(state, action);

      if(pQState == null) {
         pQState = new ParetoQState(problem.getNumberOfObjectives());
      }

      final boolean hasAdded = this.qTable.addStateIfNotExists(nextState);
      if(hasAdded) {
         this.qStateNDP.put(nextState, new NondominatedPopulation());
      }

      // Determine old action set solutions, remove from state pool
      sPool.removeAll(pQState.getAdvantages());
      pQState.getAdvantages().clear();

      pQState.setNDObjectives(qStateNDP.get(nextState));

      final INDArray rewardsArr = Nd4j.create(rewards);

      pQState.setImmediateR(
            pQState.getImmediateR().add(rewardsArr.sub(pQState.getImmediateR()).div(pQState.incUpdates())));

      for(final Solution s : pQState.getNdObjectives()) {
         final INDArray sObjectives = Nd4j.create(s.getObjectives());
         pQState.getAdvantages()
               .add(new Solution(pQState.getImmediateR().add(sObjectives.mul(gamma)).toDoubleVector()));
      }

      if(pQState.getNdObjectives().size() == 0) {
         pQState.getAdvantages().add(new Solution(pQState.getImmediateR().toDoubleVector()));
      }

      sPool.addAll(pQState.getAdvantages());

      qTable.update(state, action, pQState);
   }

}
