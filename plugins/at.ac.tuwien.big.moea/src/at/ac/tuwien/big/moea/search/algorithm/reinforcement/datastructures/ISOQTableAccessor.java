package at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures;

public interface ISOQTableAccessor<S, A> extends IQTableAccessor<S, A> {
   public A getMaxRewardAction(final S s);

   public double getMaxRewardValue(final S s);

   public int getNoOfPossibleActions(final S s);

   public double getTransitionReward(final S s, final A a);

   public void update(S state, A action, double qUpdateValue);
}
