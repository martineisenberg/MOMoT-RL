package at.ac.tuwien.big.momot.search.algorithm.reinforcement.datastructures;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IRewardStrategy;

import java.lang.reflect.InvocationTargetException;

import org.moeaframework.core.Solution;

public class RewardFactory<S extends Solution, E extends IRewardStrategy<S>> {

   public static <S extends Solution, E extends IRewardStrategy<S>> IRewardStrategy<S> createRewardStrategy(
         final Class<E> c) {
      try {
         return c.getDeclaredConstructor().newInstance();
      } catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
            | NoSuchMethodException | SecurityException e) {
         e.printStackTrace();
      }
      return null;
   }

}
