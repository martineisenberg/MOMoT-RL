package at.ac.tuwien.big.momot.search.algorithm.reinforcement.datastructures;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.IQTableAccessor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class QTable<S, A, R> implements IQTableAccessor<S, A>, Serializable {

   protected final Map<S, Map<A, R>> table;
   protected transient IModifier<S> stateModifier;
   protected transient IModifier<A> actionModifier;

   public QTable() {
      this.table = new HashMap<>();
   }

   public QTable(final IModifier<S> stateModifier, final IModifier<A> actionModifier) {
      this.table = new HashMap<>();
      this.stateModifier = stateModifier;
      this.actionModifier = actionModifier;
   }

   private boolean _addStateIfNotExists(final S s) {
      if(!this.table.containsKey(s)) {
         this.table.put(s, new HashMap<>());
         return true;
      }
      return false;
   }

   @Override
   public boolean addStateIfNotExists(final S s) {
      final S mState = this.stateModifier != null ? this.stateModifier.modify(s) : s;

      return this._addStateIfNotExists(mState);
   }

   public Map<S, Map<A, R>> getTable() {
      return this.table;
   }

   public void setActionModifier(final IModifier<A> m) {
      this.actionModifier = m;
   }

   public void setStateModifier(final IModifier<S> m) {
      this.stateModifier = m;
   }

}
