package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.local.IFitnessComparator;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IEncodingStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IRewardStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.fitness.comparator.ObjectiveFitnessComparator;
import at.ac.tuwien.big.momot.search.fitness.IEGraphMultiDimensionalFitnessFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.moeaframework.core.Solution;

public class EnvironmentBuilder<S extends Solution> {
   private IRewardStrategy<S> rewardStrategy;

   private IEncodingStrategy<S> encodingStrategy;
   private final IEGraphMultiDimensionalFitnessFunction fitnessFunction;
   private String objectiveName;

   public EnvironmentBuilder(final IEGraphMultiDimensionalFitnessFunction fitnessFunction) {
      this.fitnessFunction = fitnessFunction;
      this.rewardStrategy = null;
      this.encodingStrategy = null;
      this.objectiveName = null;
   }

   @SuppressWarnings("unchecked")
   public Map<IEnvironment.Type, IEnvironment<S>> build() {
      final Map<IEnvironment.Type, IEnvironment<S>> environmentMap = new HashMap<>();

      if(this.encodingStrategy != null && (this.objectiveName != null || this.rewardStrategy != null)) {
         environmentMap.put(IEnvironment.Type.PolicyBasedEnvironment,
               new SOEnvironment<>(new ObjectiveFitnessComparator<>(fitnessFunction.getObjectiveIndex(objectiveName)),
                     objectiveName, this.rewardStrategy, this.encodingStrategy));
      }

      if(this.objectiveName != null) {
         environmentMap.put(IEnvironment.Type.SOValueBasedEnvironment,
               new SOEnvironment<>(new ObjectiveFitnessComparator<>(fitnessFunction.getObjectiveIndex(objectiveName)),
                     objectiveName, this.rewardStrategy, this.encodingStrategy));
      }

      final List<IFitnessComparator<?, Solution>> comparatorList = fitnessFunction.getObjectiveNames().stream()
            .map(x -> fitnessFunction.getObjectiveIndex(x)).map(y -> new ObjectiveFitnessComparator<>(y))
            .collect(Collectors.toList());

      environmentMap.put(IEnvironment.Type.MOValueBasedEnvironment,
            new MOEnvironment<>(comparatorList, fitnessFunction.getObjectiveNames(), this.encodingStrategy));

      return environmentMap;

   }

   public EnvironmentBuilder<S> encodingStrategy(final IEncodingStrategy<S> strategy) {
      this.encodingStrategy = strategy;
      return this;
   }

   public EnvironmentBuilder<S> rewardStrategy(final IRewardStrategy<S> strategy) {
      this.rewardStrategy = strategy;
      return this;
   }

   public EnvironmentBuilder<S> singleObjective(final String objectiveName) {
      this.objectiveName = objectiveName;
      return this;
   }

}
