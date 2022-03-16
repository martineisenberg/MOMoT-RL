package at.ac.tuwien.big.moea.search.algorithm.reinforcement.algorithms;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.AbstractSOTabularRLAgent;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.DoneStatus;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.ISOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.SOEnvResponse;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.LocalSearchStrategy;

import java.util.List;

import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public class SingleObjectiveQLearning<S extends Solution> extends AbstractSOTabularRLAgent<S> {

   private final double gamma; // Eagerness - 0 looks in the near future, 1 looks in the distant future
   private double eps;
   private final double epsDecay;
   private final double epsMinimum;
   private final boolean withEpsDecay;
   private final LocalSearchStrategy localSearchStrategy;
   private final int exploreSteps;

   private final List<UnitApplication> f = null;

   public SingleObjectiveQLearning(final LocalSearchStrategy localSearchStrategy, final int exploreSteps,
         final double gamma, final double eps, final boolean withEpsDecay, final double epsDecay,
         final double epsMinimum, final Problem problem, final ISOEnvironment<S> environment, final String savePath,
         final int recordInterval, final int terminateAfterEpisodes, final String qTableIn, final String qTableOut,
         final boolean verbose) {
      super(problem, environment, savePath, recordInterval, terminateAfterEpisodes, qTableIn, qTableOut, verbose);
      this.gamma = gamma;
      this.eps = eps;
      this.epsDecay = epsDecay;
      this.epsMinimum = epsMinimum;
      this.withEpsDecay = withEpsDecay;
      this.localSearchStrategy = localSearchStrategy;
      this.exploreSteps = exploreSteps;

   }

   @Override
   public List<ApplicationState> epsGreedyDecision() {

      List<ApplicationState> nextAction = null;

      if(rng.nextDouble() >= this.eps) {

         // Pick best transformation (max. benefit) for current state
         nextAction = this.qTable.getMaxRewardAction(utils.getApplicationStates(currentSolution));

      } else if(withEpsDecay && eps >= epsMinimum) {
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

      final SOEnvResponse<S> response = (SOEnvResponse<S>) environment.step(localSearchStrategy, nextAction,
            exploreSteps);

      final DoneStatus doneStatus = response.getDoneStatus();
      final double reward = response.getReward();
      final S nextState = response.getState();

      if(doneStatus != DoneStatus.FINAL_STATE_REACHED) {
         cumReward += reward;
         updateQ(utils.getApplicationStates(currentSolution),
               utils.getApplicationStatesDiff(currentSolution, nextState), utils.getApplicationStates(nextState),
               reward);

         addSolutionIfImprovement(nextState);

         iterations++;

         printIfVerboseMode("Iteration: " + iterations);

      }

      if(doneStatus == DoneStatus.MAX_LENGTH_REACHED || doneStatus == DoneStatus.FINAL_STATE_REACHED) {

         if(this.saveInterval > 0) {
            rewardEarned.add(cumReward);
            framesList.add((double) iterations);
            timePassedList.add((double) (System.currentTimeMillis() - startTime));
            meanRewardEarned.add(cumReward / epochSteps);

            if(terminateAfterEpisodes > 0 && terminateAfterEpisodes <= nrOfEpochs) {
               System.out.println("Terminated after " + terminateAfterEpisodes + " seconds");
               this.terminate();
            }

            if(scoreSavePath != null && nrOfEpochs > 0 && nrOfEpochs % this.saveInterval == 0) {

               printIfVerboseMode("Saving rewards at epoch " + nrOfEpochs + " after "
                     + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");

               saveRewards(scoreSavePath, framesList, rewardEarned, timePassedList, meanRewardEarned);
            }
         }

         nrOfEpochs++;
         cumReward = 0;
         epochSteps = 0;
         currentSolution = environment.reset();
      } else {
         currentSolution = nextState;
      }

   }

   private void updateQ(final List<ApplicationState> state, final List<ApplicationState> action,
         final List<ApplicationState> nextState, final double reward) {

      final double transitionReward = this.qTable.getTransitionReward(state, action);

      this.qTable.addStateIfNotExists(nextState);

      final double qUpdateValue = transitionReward
            + (reward + gamma * this.qTable.getMaxRewardValue(nextState) - transitionReward);

      this.qTable.update(state, action, qUpdateValue);
   }

}
