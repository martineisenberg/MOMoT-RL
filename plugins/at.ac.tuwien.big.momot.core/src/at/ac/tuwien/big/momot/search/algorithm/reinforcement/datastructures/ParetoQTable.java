package at.ac.tuwien.big.momot.search.algorithm.reinforcement.datastructures;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.IParetoQTableAccessor;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ParetoQState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.EvaluationStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.Hypervolume;

public class ParetoQTable<S, A> extends QTable<S, A, ParetoQState> implements IParetoQTableAccessor<S, A> {

   private final Random rng;

   public ParetoQTable() {
      this.rng = new Random();
   }

   private A _getActionForCurrentState(final NondominatedPopulation stateQNDP, final S state) {
      for(final Entry<A, ParetoQState> entry : this.table.get(state).entrySet()) {
         for(final Solution s : entry.getValue().getAdvantages()) {
            if(Arrays.equals(s.getObjectives(), stateQNDP.get(0).getObjectives())) {
               return entry.getKey();
            }

         }
      }
      return null;
   }

   private A _getMaxRewardAction(final EvaluationStrategy strategy, final S state, final Problem p,
         final NondominatedPopulation qndp) {

      switch(strategy) {
         case HYPERVOLUME:

            double maxHV = 0.0;
            Hypervolume hv = null;
            A nextAction = null;

            try {
               hv = new Hypervolume(p, qndp);
            } catch(final IllegalArgumentException e1) {
               return null;
            }

            for(final Entry<A, ParetoQState> entry : this.table.get(state).entrySet()) {
               final NondominatedPopulation curNdp = new NondominatedPopulation(entry.getValue().getAdvantages());
               final double curHV = hv.evaluate(curNdp);

               if(maxHV < curHV) {
                  maxHV = curHV;
                  nextAction = entry.getKey();
               }
            }
            return nextAction;
         case CARDINALITY:
            throw new IllegalArgumentException("Cardinality evaluation strategy not implemented yet!");
         case PARETO:
            final List<A> ndActionKeyList = new ArrayList<>();

            for(final Entry<A, ParetoQState> entry : this.table.get(state).entrySet()) {
               for(final Solution s : entry.getValue().getAdvantages()) {
                  if(qndp.contains(s)) {
                     ndActionKeyList.add(entry.getKey());
                     break;
                  }
               }
            }

            if(ndActionKeyList.size() == 0) {
               return null;
            }
            nextAction = ndActionKeyList.get(this.rng.nextInt(ndActionKeyList.size()));
      }
      return null;

   }

   @Override
   public A getActionForCurrentState(final NondominatedPopulation stateQNDP, final S state) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(state) : state;
      final A maxRewA = this._getActionForCurrentState(stateQNDP, mState);

      return maxRewA == null ? null
            : this.actionModifier == null ? maxRewA : this.actionModifier.reverseModify(maxRewA);

   }

   @Override
   public A getMaxRewardAction(final EvaluationStrategy strategy, final S s, final Problem p,
         final NondominatedPopulation qndp) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;
      final A maxRewA = this._getMaxRewardAction(strategy, mState, p, qndp);

      return maxRewA == null ? null
            : this.actionModifier == null ? maxRewA : this.actionModifier.reverseModify(maxRewA);

   }

   @Override
   public ParetoQState getParetoQState(final S s, final A a) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;
      final A mAction = this.actionModifier != null ? this.actionModifier.modify(a) : a;

      return this.table.get(mState).get(mAction);
   }

   @Override
   public void update(final S s, final A a, final ParetoQState pQState) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;
      final A mAction = this.actionModifier != null ? this.actionModifier.modify(a) : a;

      this.table.get(mState).put(mAction, pQState);

   }

}
