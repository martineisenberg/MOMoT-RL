package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.problem.solution.variable.IPlaceholderVariable;
import at.ac.tuwien.big.moea.search.algorithm.local.IFitnessComparator;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IEncodingStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IMOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.MOEnvResponse;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.LocalSearchStrategy;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.Solution;

public class MOEnvironment<S extends Solution> extends AbstractEnvironment<S> implements IMOEnvironment<S> {
   protected List<IFitnessComparator<?, Solution>> fitnessComparatorList;
   protected List<String> objectiveNames;

   public MOEnvironment(final List<IFitnessComparator<?, Solution>> fitnessComparatorList,
         final List<String> objectiveNames, final IEncodingStrategy<S> encodingStrategy) {
      super(encodingStrategy);
      this.fitnessComparatorList = fitnessComparatorList;
      this.objectiveNames = objectiveNames;
   }

   private double[] determineRewards(final S state) {

      final double[] rewardVec = new double[fitnessComparatorList.size()];
      for(int i = 0; i < fitnessComparatorList.size(); i++) {
         rewardVec[i] = (Double) fitnessComparatorList.get(i).getValue(state) * -1;
      }

      return rewardVec;
   }

   @Override
   public List<String> getFunctionNames() {
      return this.objectiveNames;

   }

   @Override
   public double[] getRewards(final S state) {
      return this.determineRewards(state);
   }

   private S localSearchMO(final List<S> solutions) {
      final double[] curObjRewards = this.determineRewards(currentState);
      double curMaxGain = Double.NEGATIVE_INFINITY;
      S nextState = null;

      for(final S s : solutions) {

         evaluteSolution(s);

         if(s.getNumberOfVariables() == this.currentState.getNumberOfVariables() || s.getNumberOfVariables() > 0
               && s.getVariable(s.getNumberOfVariables() - 1) instanceof IPlaceholderVariable) {
            continue;
         }

         final double[] solRewards = determineRewards(s);
         for(int i = 0; i < fitnessComparatorList.size(); i++) {
            final double gain = solRewards[i] - curObjRewards[i];
            if(gain > curMaxGain) {
               nextState = s;
               curMaxGain = gain;
            }
         }
      }
      return nextState;
   }

   @Override
   public MOEnvResponse<S> step(final LocalSearchStrategy strategy, final List<ApplicationState> action,
         final int explorationSteps) {
      final MOEnvResponse<S> response = new MOEnvResponse<>();

      S nextState = null;

      if(action == null) {

         switch(strategy) {
            case GREEDY:
               final List<S> solutions = new ArrayList<>();
               for(final S solution : this.solutionProvider.generateNeighbors(currentState, explorationSteps, null)) {
                  solutions.add(solution);
               }
               nextState = this.localSearchMO(solutions);
               break;
            case NONE:
               for(final S solution : this.solutionProvider.generateNeighbors(currentState, 1, null)) {
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
         response.setRewards(determineRewards(nextState));
         response.setState(nextState);
         currentState = nextState;
      }

      return response;
   }

}
