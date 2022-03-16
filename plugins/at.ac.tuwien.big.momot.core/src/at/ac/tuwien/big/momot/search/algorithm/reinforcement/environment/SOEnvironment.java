package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.problem.solution.variable.IPlaceholderVariable;
import at.ac.tuwien.big.moea.search.algorithm.local.IFitnessComparator;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IEncodingStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IRewardStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.ISOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.SOEnvResponse;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.LocalSearchStrategy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.henshin.interpreter.RuleApplication;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;

public class SOEnvironment<S extends Solution> extends AbstractEnvironment<S> implements ISOEnvironment<S> {

   protected IFitnessComparator<?, Solution> fitnessComparator;
   private final IRewardStrategy<S> rewardStrategy;
   private final String objectiveName;

   public SOEnvironment(final IFitnessComparator<?, Solution> fitnessComparator, final String objectiveName,
         final IRewardStrategy<S> rewardStrategy, final IEncodingStrategy<S> encodingStrategy) {
      super(encodingStrategy);
      this.rewardStrategy = rewardStrategy;
      this.fitnessComparator = fitnessComparator;
      this.objectiveName = objectiveName;
   }

   private double determineReward(final S state) {
      if(this.rewardStrategy != null && this.rewardStrategy.getRewardMap() != null) {

         return this.determineRewardByTransformations(this.utils.getApplicationStatesDiff(currentState, state))
               + this.rewardStrategy.determineAdditionalReward(state);
      }
      return this.determineRewardByFitnessFunction(state);
   }

   private double determineRewardByFitnessFunction(final S state) {
      return (Double) fitnessComparator.getValue(state) * -1;
   }

   private double determineRewardByTransformations(final List<ApplicationState> ruleAssignments) {
      double reward = 0;
      for(final ApplicationState actionRule : ruleAssignments) {
         final String ruleName = actionRule.getUnit().getName();
         for(final RuleApplication rule : actionRule.getAppliedRules()) {
            if(rewardStrategy.getRewardMap().containsKey(rule.getRule().getName())) {
               reward += rewardStrategy.getRewardMap().get(rule.getRule().getName());
            }
         }
      }

      return reward;
   }

   @Override
   public String getFunctionName() {
      return this.objectiveName;
   }

   @Override
   public IEncodingStrategy<S> getProblemEncoder() {
      return this.encodingStrategy;
   }

   @Override
   public double getReward(final S state) {
      return this.determineReward(state);
   }

   private S localSearchSO(final List<S> solutions) {
      double maxReward = Double.NEGATIVE_INFINITY;
      S nextState = null;

      for(final S solution : solutions) {

         evaluteSolution(solution);

         if(solution.getNumberOfVariables() == this.currentState.getNumberOfVariables()
               || solution.getNumberOfVariables() > 0
                     && solution.getVariable(solution.getNumberOfVariables() - 1) instanceof IPlaceholderVariable) {
            continue;
         }

         final double nextStateReward = determineReward(solution);

         if(nextStateReward > maxReward) {
            maxReward = nextStateReward;
            nextState = solution;
         }
      }

      return nextState;
   }

   @Override
   public SOEnvResponse<S> step(final LocalSearchStrategy strategy, final List<ApplicationState> action,
         final int explorationSteps) {
      final SOEnvResponse<S> response = new SOEnvResponse<>();

      S nextState = null;

      if(action == null) {

         switch(strategy) {
            case GREEDY:
               final List<S> solutions = new ArrayList<>();

               for(final S solution : this.solutionProvider.generateNeighbors(currentState, explorationSteps,
                     this.encodingStrategy)) {
                  solutions.add(solution);
               }

               nextState = this.localSearchSO(solutions);
               break;
            case NONE:
               for(final S solution : this.solutionProvider.generateNeighbors(currentState, 1, this.encodingStrategy)) {
                  nextState = solution;
               }
               evaluteSolution(nextState);
               break;
         }

      } else {
         nextState = this.solutionProvider.generateExtendedSolution(this.currentState, action);
         evaluteSolution(nextState);
      }

      response.setDone(determineIsEpisodeDone(nextState));

      if(nextState != null && nextState.getNumberOfVariables() > currentState.getNumberOfVariables()) {
         response.setReward(determineReward(nextState));
         response.setState(nextState);
         currentState = nextState;

      }

      return response;
   }

   @Override
   public SOEnvResponse<S> step(final S solution, final INDArray actionProbs) {
      final SOEnvResponse<S> response = new SOEnvResponse<>();

      final Object[] actionSolution = this.solutionProvider.generateExtendedSolution(this.encodingStrategy, solution,
            actionProbs);
      final int action = (int) actionSolution[0];
      final S nextState = (S) actionSolution[1];

      response.setState(nextState);
      response.setAppliedActionId(action);

      evaluteSolution(nextState);

      response.setReward(determineReward(nextState));
      response.setDone(determineIsEpisodeDone(nextState));

      currentState = nextState;

      return response;
   }

}
