package at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain.strategies;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IRewardStrategy;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.util.MomotUtil;

import java.util.HashMap;
import java.util.Map;

import org.moeaframework.core.Solution;

import container.ContainerModel;

public class ContainerRewardStrategy<S extends Solution> implements IRewardStrategy<S> {

   @Override
   public double determineAdditionalReward(final S s) {
      final ContainerModel model = MomotUtil.getRoot(((TransformationSolution) s).getResultGraph(),
            ContainerModel.class);
      final int retrievedContainers = (int) model.getContainer().stream().filter(c -> c.getOn() == null).count();

      return retrievedContainers - model.getContainer().size() == 0 ? 50 : 0;
   }

   @Override
   public Map<String, Double> getRewardMap() {
      final Map<String, Double> rewardMap = new HashMap<>();
      rewardMap.put("retrieveNonLastFromStack", 10.0);
      rewardMap.put("retrieveLastFromStack", 10.0);
      rewardMap.put("retrieveOnTopOfSuccessorFromStack", 10.0);
      rewardMap.put("retrieveLastOverallFromStack", 10.0);
      rewardMap.put("relocateNonLastOnStackToEmptyStack", -6.0);
      rewardMap.put("relocateNonLastOnStackToNonEmptyStack", -6.0);
      rewardMap.put("relocateLastOnStackToNonEmptyStack", -6.0);

      return rewardMap;
   }

}
