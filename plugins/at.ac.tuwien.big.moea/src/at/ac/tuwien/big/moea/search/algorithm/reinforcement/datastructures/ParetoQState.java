package at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class ParetoQState {
   private NondominatedPopulation ndObjectives;
   private INDArray immediateR;
   private List<Solution> advantages;
   private int updates;

   public ParetoQState(final int noObjectives) {
      this.ndObjectives = new NondominatedPopulation();
      this.advantages = new ArrayList<>();
      this.immediateR = Nd4j.zeros(1, noObjectives);
   }

   public List<Solution> getAdvantages() {
      return advantages;
   }

   public INDArray getImmediateR() {
      return immediateR;
   }

   public NondominatedPopulation getNdObjectives() {
      return ndObjectives;
   }

   public int getUpdates() {
      return updates;
   }

   public int incUpdates() {
      this.updates++;
      return this.updates;
   }

   public void setAdvantages(final List<Solution> advantages) {
      this.advantages = advantages;
   }

   public void setImmediateR(final INDArray immediateR) {
      this.immediateR = immediateR;
   }

   public void setNDObjectives(final NondominatedPopulation p) {
      this.ndObjectives = p;
   }

   public void setUpdates(final int updates) {
      this.updates = updates;
   }

}
