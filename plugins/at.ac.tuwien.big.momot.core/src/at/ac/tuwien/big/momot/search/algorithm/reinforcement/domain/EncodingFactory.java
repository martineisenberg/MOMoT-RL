package at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IEncodingStrategy;

import java.lang.reflect.InvocationTargetException;

import org.moeaframework.core.Solution;

public class EncodingFactory<S extends Solution, E extends IEncodingStrategy<S>> {

   public static <S extends Solution, E extends IEncodingStrategy<S>> IEncodingStrategy<S> createEncoder(final Class<E> c) {
      try {
         return c.getDeclaredConstructor().newInstance();
      } catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
            | NoSuchMethodException | SecurityException e) {
         e.printStackTrace();
      }
      return null;
   }

}
