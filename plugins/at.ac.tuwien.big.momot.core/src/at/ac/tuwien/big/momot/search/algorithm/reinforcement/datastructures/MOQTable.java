package at.ac.tuwien.big.momot.search.algorithm.reinforcement.datastructures;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.IMOQTableAccessor;

import java.util.Map;

public class MOQTable<S, A> extends QTable<S, A, double[]> implements IMOQTableAccessor<S, A> {

   private A _getMaxRewardAction(final S s, final int agent) {
      if(!this.table.containsKey(s)) {
         return null;
      }

      A action = null;
      double maxReward = Double.NEGATIVE_INFINITY;
      final Map<A, double[]> actionTable = this.table.get(s);

      for(final A choosableAction : actionTable.keySet()) {
         if(actionTable.get(choosableAction)[agent] > maxReward) {
            action = choosableAction;
            maxReward = actionTable.get(choosableAction)[agent];
         }
      }
      return action;
   }

   private double _getMaxRewardValue(final S s, final int agent) {
      if(this.table.get(s).isEmpty()) {
         return 0;
      }

      double maxReward = Double.NEGATIVE_INFINITY;

      final Map<A, double[]> actionTable = this.table.get(s);
      for(final A choosableAction : actionTable.keySet()) {
         if(actionTable.get(choosableAction)[agent] > maxReward) {
            maxReward = actionTable.get(choosableAction)[agent];
         }
      }

      return maxReward;
   }

   private double _getTransitionReward(final S s, final A a, final int agent) {
      if(!this.table.get(s).containsKey(a)) {
         return 0;
      }

      return this.table.get(s).get(a)[agent];
   }

   @Override
   public boolean containsKey(final S s) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;

      return this.table.containsKey(mState);
   }

   @Override
   public Map<A, double[]> getActionMap(final S s) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;

      return this.table.get(mState);
   }

   @Override
   public A getMaxRewardAction(final S s, final int agent) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;
      final A maxRewA = this._getMaxRewardAction(mState, agent);

      return maxRewA == null ? null
            : this.actionModifier == null ? maxRewA : this.actionModifier.reverseModify(maxRewA);
   }

   @Override
   public double getMaxRewardValue(final S s, final int agent) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;

      return this._getMaxRewardValue(mState, agent);
   }

   @Override
   public double getTransitionReward(final S s, final A a, final int agent) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;
      final A mAction = this.actionModifier != null ? this.actionModifier.modify(a) : a;

      return this._getTransitionReward(mState, mAction, agent);
   }

   @Override
   public void update(final S s, final A a, final double[] qUpdateValues) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;
      final A mAction = this.actionModifier != null ? this.actionModifier.modify(a) : a;

      this.table.get(mState).put(mAction, qUpdateValues);

   }

}
