package at.ac.tuwien.big.moea.search.algorithm.reinforcement.algorithms;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.AbstractMOTabularRLAgent;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.DoneStatus;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IMOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.MOEnvResponse;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.LocalSearchStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public class WeightedQLearning<S extends Solution> extends AbstractMOTabularRLAgent<S> {
   private final double gamma;
   private double eps;

   private final double epsDecay;
   private final double epsMinimum;
   private final boolean withEpsDecay;
   private final LocalSearchStrategy localSearchStrategy;
   private final int exploreSteps;

   private final double[] w;
   private final double[] z;
   private final double tau;

   public WeightedQLearning(final double[] w, final double tau, final LocalSearchStrategy localSearchStrategy,
         final int exploreSteps, final double gamma, final double eps, final boolean withEpsDecay,
         final double epsDecay, final double epsMinimum, final Problem problem, final IMOEnvironment<S> environment,
         final String savePath, final int recordInterval, final int terminateAfterEpisodes, final String qTableIn,
         final String qTableOut, final boolean verbose) {
      super(problem, environment, savePath, recordInterval, terminateAfterEpisodes, qTableIn, qTableOut, verbose);

      this.gamma = gamma;
      this.eps = eps;
      this.w = w;
      this.tau = tau;
      this.localSearchStrategy = localSearchStrategy;
      this.exploreSteps = exploreSteps;
      this.epsDecay = epsDecay;
      this.epsMinimum = epsMinimum;
      this.withEpsDecay = withEpsDecay;

      this.z = this.environment.getRewards(currentSolution);

   }

   private double chebyshevMaxActionValue(final List<ApplicationState> s, final int agentIdx) {
      final List<ApplicationState> action = this.chebyshevSelection(s);
      if(action == null) {
         return 0;
      }

      return qTable.getActionMap(s).get(action)[agentIdx];
   }

   private List<ApplicationState> chebyshevSelection(final List<ApplicationState> state) {
      final Map<List<ApplicationState>, Double> sqMap = new HashMap<>();

      if(!qTable.containsKey(state)) {
         return null;
      }

      final Map<List<ApplicationState>, double[]> actionTable = qTable.getActionMap(state);

      for(final List<ApplicationState> choosableAction : actionTable.keySet()) {
         double maxObjVal = Double.NEGATIVE_INFINITY;
         for(int i = 0; i < problem.getNumberOfObjectives() - 1; i++) {
            final double cur_w = this.w[i] * Math.abs(actionTable.get(choosableAction)[i] - z[i]);
            if(cur_w > maxObjVal) {
               maxObjVal = cur_w;
            }

         }
         sqMap.put(choosableAction, maxObjVal);
      }

      if(sqMap.isEmpty()) {
         return null;
      }

      return sqMap.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();

   }

   @Override
   public List<ApplicationState> epsGreedyDecision() {
      List<ApplicationState> nextAction = null;

      if(rng.nextDouble() >= this.eps) {
         nextAction = chebyshevSelection(utils.getApplicationStates(currentSolution));
      } else if(withEpsDecay && eps >= epsMinimum) { // nextAction = null => explore and decrease eps if above threshold
         eps -= epsDecay;
      }

      return nextAction;
   }

   @Override
   protected void iterate() {
      if(this.startTime == 0) {
         this.startTime = System.currentTimeMillis();
      }

      final List<ApplicationState> nextAction = epsGreedyDecision();
      MOEnvResponse<S> response = null;

      response = (MOEnvResponse<S>) environment.step(localSearchStrategy, nextAction, exploreSteps);

      final DoneStatus doneStatus = response.getDoneStatus();
      final double[] rewards = response.getRewards();
      final S nextState = response.getState();

      if(doneStatus != DoneStatus.FINAL_STATE_REACHED) {
         for(int i = 0; i < rewardEarnedLists.size(); i++) {
            cumRewardList.set(i, cumRewardList.get(i) + rewards[i]);
         }

         updateQ(utils.getApplicationStates(currentSolution),
               utils.getApplicationStatesDiff(currentSolution, nextState), utils.getApplicationStates(nextState),
               rewards);

         addSolutionIfImprovement(nextState);

         iterations++;
         printIfVerboseMode("Iteration: " + iterations);

      }

      iterations++;
      if(doneStatus == DoneStatus.MAX_LENGTH_REACHED || doneStatus == DoneStatus.FINAL_STATE_REACHED) {
         if(this.saveInterval > 0) {

            for(int i = 0; i < rewardEarnedLists.size(); i++) {
               rewardEarnedLists.get(i).add(cumRewardList.get(i));
               meanRewardEarnedLists.get(i).add(cumRewardList.get(i) / epochSteps);
            }
            framesList.add((double) iterations);
            timePassedList.add((double) (System.currentTimeMillis() - startTime));

            if(scoreSavePath != null && nrOfEpochs > 0 && nrOfEpochs % this.saveInterval == 0) {

               saveRewards(scoreSavePath, framesList, this.environment.getFunctionNames(), rewardEarnedLists,
                     timePassedList, meanRewardEarnedLists);
            }
         }

         nrOfEpochs++;
         cumRewardList = new ArrayList<>(Collections.nCopies(problem.getNumberOfObjectives(), 0.0));
         epochSteps = 0;
         currentSolution = environment.reset();
      } else {
         currentSolution = nextState;
      }
   }

   private void updateQ(final List<ApplicationState> state, final List<ApplicationState> action,
         final List<ApplicationState> nextState, final double[] rewards) {

      qTable.addStateIfNotExists(nextState);

      // update z* if necessary, max. function values are returned with *-1
      for(int i = 0; i < z.length; i++) {
         if(z[i] < rewards[i]) {
            z[i] = rewards[i] + tau;
         }
      }

      int agentIdx = 0;
      final double[] qUpdateValues = new double[rewards.length];

      for(final double agentReward : rewards) {

         final double transitionReward = qTable.getTransitionReward(state, action, agentIdx);

         final double qUpdateValue = transitionReward
               + (agentReward + gamma * chebyshevMaxActionValue(nextState, agentIdx) - transitionReward);

         qUpdateValues[agentIdx] = qUpdateValue;
         agentIdx++;
      }

      qTable.update(state, action, qUpdateValues);

   }
}
