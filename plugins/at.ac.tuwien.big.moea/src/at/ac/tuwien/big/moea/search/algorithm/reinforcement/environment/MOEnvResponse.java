package at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment;

import org.moeaframework.core.Solution;

public class MOEnvResponse<S extends Solution> extends EnvResponse<S> {
   private double[] rewards;

   public double[] getRewards() {
      return rewards;
   }

   public void setRewards(final double[] rewards) {
      this.rewards = rewards;
   }

}
