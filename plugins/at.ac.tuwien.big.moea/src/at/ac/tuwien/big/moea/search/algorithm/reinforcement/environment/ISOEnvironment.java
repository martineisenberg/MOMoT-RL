package at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment;

import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface ISOEnvironment<S extends Solution> extends IEnvironment<S> {
   public String getFunctionName();

   double getReward(final S state);

   SOEnvResponse<S> step(final S solution, final INDArray actionProbs);

}
