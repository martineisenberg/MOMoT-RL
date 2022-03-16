package at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IEncodingStrategy;

import org.moeaframework.core.Solution;

public abstract class AbstractEncodingStrategy<S extends Solution> implements IEncodingStrategy<S> {

   public int[] integerToOnehot(final int integer, final int bits) {
      final int[] state = new int[bits];
      for(int i = 1; i <= bits; i++) {
         if(i == integer) {
            state[bits - i] = 1;
         } else {
            state[bits - i] = 0;
         }
      }

      return state;
   }
}
