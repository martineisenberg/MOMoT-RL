package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IEncodingStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.DoneStatus;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.ISolutionExtender;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IRLUtils;
import at.ac.tuwien.big.momot.problem.solution.variable.ITransformationVariable;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.algorithm.RLUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;

public abstract class AbstractEnvironment<S extends Solution> implements IEnvironment<S> {

   protected S initialState = null;
   protected S currentState = null;
   protected ISolutionExtender<S> solutionProvider;
   protected int maxSolutionLength;
   protected final IEncodingStrategy<S> encodingStrategy;

   protected final IRLUtils<S> utils;

   private Object agentClass = null;
   private Method evaluateFunction = null;

   public AbstractEnvironment(final IEncodingStrategy<S> encodingStrategy) {
      this.utils = new RLUtils<>();
      this.encodingStrategy = encodingStrategy;
   }

   public DoneStatus determineIsEpisodeDone(final S nextState) {

      if(nextState == null || determineIsFinalState(nextState)) {
         return DoneStatus.FINAL_STATE_REACHED;
      }

      if(encodingStrategy != null && (determineIsEpisodeDone(nextState, encodingStrategy)
            || encodingStrategy.isEpisodeEndingState(nextState))) {
         return DoneStatus.FINAL_STATE_REACHED;
      }

      if(nextState.getNumberOfVariables() >= this.maxSolutionLength) {
         return DoneStatus.MAX_LENGTH_REACHED;
      }

      return null;
   }

   private boolean determineIsEpisodeDone(final S s, final IEncodingStrategy<S> encoder) {
      final List<String> endingRules = encoder.getEpisodeEndingRules();
      if(endingRules != null) {
         for(int i = 0; i < s.getNumberOfVariables(); i++) {
            final Variable var = s.getVariable(i);
            if(endingRules.contains(((ITransformationVariable) var).getUnit().getName())) {
               return true;
            }
         }
      }
      return false;
   }

   protected boolean determineIsFinalState(final S nextState) {
      return this.currentState.getNumberOfVariables() == nextState.getNumberOfVariables();
   }

   public void evaluteSolution(final S state) {
      try {
         evaluateFunction.invoke(agentClass, state);
      } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
         e.printStackTrace();
      }
   }

   @Override
   public S getInitialSolution() {
      return utils.newTransformationSolution(initialState);
   }

   @Override
   public IEncodingStrategy<S> getProblemEncoder() {
      return this.encodingStrategy;
   }

   @Override
   public IRLUtils<S> getRLUtils() {
      return this.utils;
   }

   @Override
   public Map<String, Unit> getUnitMapping() {
      return this.solutionProvider.getUnitMapping();
   }

   @Override
   public S reset() {
      currentState = utils.newTransformationSolution(initialState);
      return currentState;
   }

   @Override
   public void setEvaluationMethod(final Object agentInstance, final Method evaluateFunction) {
      this.agentClass = agentInstance;
      this.evaluateFunction = evaluateFunction;
   }

   @Override
   public void setInitialSolution(final S solution) {
      this.initialState = utils.newTransformationSolution(solution);
   }

   @Override
   public void setSolutionLength(final int length) {
      this.maxSolutionLength = length;
   }

   @Override
   public void setSolutionProvider(final ISolutionExtender<S> provider) {
      this.solutionProvider = provider;
   }

}
