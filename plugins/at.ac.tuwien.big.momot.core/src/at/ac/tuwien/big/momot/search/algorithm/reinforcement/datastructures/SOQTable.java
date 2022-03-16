package at.ac.tuwien.big.momot.search.algorithm.reinforcement.datastructures;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ISOQTableAccessor;

import java.util.Map;

public class SOQTable<S, A> extends QTable<S, A, Double> implements ISOQTableAccessor<S, A> {

   private A _getMaxRewardAction(final S s) {
      if(!this.table.containsKey(s)) {
         return null;
      }

      A a = null;
      double maxReward = Double.NEGATIVE_INFINITY;
      final Map<A, Double> actionTable = this.table.get(s);

      for(final A choosableAction : actionTable.keySet()) {
         if(actionTable.get(choosableAction) > maxReward) {
            a = choosableAction;
            maxReward = actionTable.get(choosableAction);
         }
      }
      return a;
   }

   private double _getMaxRewardValue(final S s) {

      if(this.table.get(s).isEmpty()) {
         return 0;
      }

      double maxReward = Double.NEGATIVE_INFINITY;

      final Map<A, Double> actionTable = this.table.get(s);
      for(final A choosableAction : actionTable.keySet()) {
         if(actionTable.get(choosableAction) > maxReward) {
            maxReward = actionTable.get(choosableAction);
         }
      }

      return maxReward;
   }

   private int _getNoOfPossibleActions(final S s) {
      if(this.table.get(s).isEmpty()) {
         return 0;
      }

      return this.table.get(s).keySet().size();

   }

   private double _getTransitionReward(final S s, final A a) {

      if(!this.table.get(s).containsKey(a)) {
         return 0;
      }

      return this.table.get(s).get(a);
   }

   @Override
   public A getMaxRewardAction(final S s) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;
      final A maxRewA = this._getMaxRewardAction(mState);

      return maxRewA == null ? null
            : this.actionModifier == null ? maxRewA : this.actionModifier.reverseModify(maxRewA);
   }

   @Override
   public double getMaxRewardValue(final S s) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;

      return this._getMaxRewardValue(mState);
   }

   @Override
   public int getNoOfPossibleActions(final S s) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;
      return this._getNoOfPossibleActions(mState);
   }

   @Override
   public double getTransitionReward(final S s, final A a) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;
      final A mAction = this.actionModifier != null ? this.actionModifier.modify(a) : a;

      return this._getTransitionReward(mState, mAction);
   }

   @Override
   public void update(final S s, final A a, final double qUpdateValue) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;
      final A mAction = this.actionModifier != null ? this.actionModifier.modify(a) : a;

      this.table.get(mState).put(mAction, qUpdateValue);
   }

}
