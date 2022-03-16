package at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain;

import java.util.List;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface IEncodingStrategy<S extends Solution> {
   List<UnitParameter> createBaseRules();

   List<UnitParameter> createPostBaseRules();

   INDArray[] encodeSolution(final S s);

   int getActionSpace(final S initialSolution);

   ComputationGraph getActorArchitecture(final S solution, final double lr, final double l2Regularization,
         final double dropoutRate, final boolean printArchitecture);

   ComputationGraph getCriticArchitecture(final S solution, final double lr, final double l2Regularization,
         final double dropoutRate, final boolean printArchitecture);

   String getDistrActionProbabilities(INDArray dist, S solution, boolean renormalizeIllegalActions);

   List<String> getEpisodeEndingRules();

   FixedRuleApplicationStrategy getFixedUnitApplicationStrategy(final S s, final int action);

   ComputationGraph getNetworkArchitecture(final S solution, final double lr, final double l2Regularization,
         final double dropoutRate, final boolean printArchitecture);

   int getStateSpace(final S initialSolution);

   boolean isEpisodeEndingState(S s);

   boolean isFinalModel(final S s);

   void printModel(final S solution);

}
