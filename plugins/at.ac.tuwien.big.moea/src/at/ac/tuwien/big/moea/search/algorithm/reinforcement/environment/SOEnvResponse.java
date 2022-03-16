package at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment;

import org.moeaframework.core.Solution;

public class SOEnvResponse<S extends Solution> extends EnvResponse<S> {
   private double reward;

   public double getReward() {
      return reward;
   }

   public void setReward(final double reward) {
      this.reward = reward;
   }

}
