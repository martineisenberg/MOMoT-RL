package at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures;

import java.util.Map;

public interface IMOQTableAccessor<S, A> extends IQTableAccessor<S, A> {

   public boolean containsKey(final S s);

   public Map<A, double[]> getActionMap(final S s);

   public A getMaxRewardAction(final S s, int agent);

   public double getMaxRewardValue(final S s, int agent);

   public double getTransitionReward(final S s, final A a, int agent);

   public void update(S state, A action, double[] qUpdateValues);
}
