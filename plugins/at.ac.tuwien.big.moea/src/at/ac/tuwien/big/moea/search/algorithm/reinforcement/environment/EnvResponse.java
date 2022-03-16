package at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment;

import org.moeaframework.core.Solution;

public abstract class EnvResponse<S extends Solution> {
   private S state;
   private DoneStatus done;
   private int appliedActionId;

   public int getAppliedAction() {
      return appliedActionId;
   }

   public int getAppliedActionId() {
      return appliedActionId;
   }

   public DoneStatus getDoneStatus() {
      return done;
   }

   public S getState() {
      return state;
   }

   public void setAppliedActionId(final int appliedActionId) {
      this.appliedActionId = appliedActionId;
   }

   public void setDone(final DoneStatus done) {
      this.done = done;
   }

   public void setState(final S state) {
      this.state = state;
   }

}
