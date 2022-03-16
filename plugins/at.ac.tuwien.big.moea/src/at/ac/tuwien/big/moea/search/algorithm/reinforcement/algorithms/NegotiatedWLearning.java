package at.ac.tuwien.big.moea.search.algorithm.reinforcement.algorithms;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.AbstractMOTabularRLAgent;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.DoneStatus;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IMOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.MOEnvResponse;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.LocalSearchStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public class NegotiatedWLearning<S extends Solution> extends AbstractMOTabularRLAgent<S> {
   private final double gamma;
   private double eps;
   private final double epsDecay;
   private final double epsMinimum;
   private final boolean withEpsDecay;
   private final int exploreSteps;
   private final LocalSearchStrategy localSearchStrategy;

   protected int[] changeCounts;

   public NegotiatedWLearning(final LocalSearchStrategy localSearchStrategy, final int exploreSteps, final double gamma,
         final double eps, final boolean withEpsDecay, final double epsDecay, final double epsMinimum,
         final Problem problem, final IMOEnvironment<S> environment, final String savePath, final int recordInterval,
         final int terminateAfterEpisodes, final String qTableIn, final String qTableOut, final boolean verbose) {
      super(problem, environment, savePath, recordInterval, terminateAfterEpisodes, qTableIn, qTableOut, verbose);

      this.gamma = gamma;
      this.eps = eps;
      this.epsDecay = epsDecay;
      this.epsMinimum = epsMinimum;
      this.withEpsDecay = withEpsDecay;
      this.exploreSteps = exploreSteps;
      this.changeCounts = new int[problem.getNumberOfObjectives()];
      this.localSearchStrategy = localSearchStrategy;

   }

   @Override
   public List<ApplicationState> epsGreedyDecision() {

      List<ApplicationState> nextAction = null;

      if(rng.nextDouble() >= this.eps) {
         // Pick best transformation (max. benefit) for current state
         int maxAgentIdx = rng.nextInt(problem.getNumberOfObjectives() - 1);
         double maxWeight = 0;

         nextAction = qTable.getMaxRewardAction(utils.getApplicationStates(currentSolution), maxAgentIdx);

         if(nextAction == null) {
            return null;
         }

         boolean converged = true;

         do {
            int curAgentIdx = 0;
            converged = true;
            for(int i = 0; i < problem.getNumberOfObjectives() - 1; i++) {
               if(curAgentIdx == maxAgentIdx) {
                  curAgentIdx++;
                  continue;
               }

               final double curAgentWeight = qTable.getMaxRewardValue(utils.getApplicationStates(currentSolution),
                     curAgentIdx)
                     - qTable.getTransitionReward(utils.getApplicationStates(currentSolution), nextAction, curAgentIdx);
               if(curAgentWeight > maxWeight) {
                  maxWeight = curAgentWeight;
                  nextAction = qTable.getMaxRewardAction(utils.getApplicationStates(currentSolution), curAgentIdx);
                  maxAgentIdx = curAgentIdx;
                  converged = false;
               }
               curAgentIdx++;
            }
         } while(!converged);
         changeCounts[maxAgentIdx]++;
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

      int agentIdx = 0;
      final double[] qUpdateValues = new double[rewards.length];

      for(final double agentReward : rewards) {

         final double transitionReward = qTable.getTransitionReward(state, action, agentIdx);

         final double qUpdateValue = transitionReward
               + (agentReward + gamma * qTable.getMaxRewardValue(nextState, agentIdx) - transitionReward);

         qUpdateValues[agentIdx] = qUpdateValue;
         agentIdx++;
      }

      // qTable.get(state).put(action, qUpdateValues);
      qTable.update(state, action, qUpdateValues);
   }
}
