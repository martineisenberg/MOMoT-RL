package at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.local.INeighborhood;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IEncodingStrategy;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface ISolutionExtender<S extends Solution> {

   Object[] generateExtendedSolution(final IEncodingStrategy<S> encoder, final S solution, final INDArray distribution);

   S generateExtendedSolution(S currentState, List<ApplicationState> action);

   INeighborhood<S> generateNeighbors(S solution, int maxNeighbors, IEncodingStrategy<S> encodingStrategy);

   Map<String, Unit> getUnitMapping();

}
