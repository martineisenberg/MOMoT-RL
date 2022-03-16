package at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.EvaluationStrategy;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;

public interface IParetoQTableAccessor<S, A> extends IQTableAccessor<S, A> {

   public A getActionForCurrentState(final NondominatedPopulation stateQNDP, final S s);

   public A getMaxRewardAction(EvaluationStrategy strategy, final S s, final Problem p,
         final NondominatedPopulation qndp);

   public ParetoQState getParetoQState(S state, A action);

   public void update(S state, A action, ParetoQState pQState);

}
