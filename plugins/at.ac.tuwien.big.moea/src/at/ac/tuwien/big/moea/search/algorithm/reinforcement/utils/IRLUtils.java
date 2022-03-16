package at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.IMOQTableAccessor;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.IParetoQTableAccessor;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.IQTableAccessor;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ISOQTableAccessor;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Solution;

public interface IRLUtils<S extends Solution> {

   List<ApplicationState> getApplicationStates(final S solution);

   List<ApplicationState> getApplicationStatesDiff(final S cur, final S next);

   IMOQTableAccessor<List<ApplicationState>, List<ApplicationState>> initMOQTable(Map<String, Unit> unitMapping);

   IParetoQTableAccessor<List<ApplicationState>, List<ApplicationState>> initParetoQTable(
         Map<String, Unit> unitMapping);

   ISOQTableAccessor<List<ApplicationState>, List<ApplicationState>> initSOQTable(Map<String, Unit> unitMapping);

   IMOQTableAccessor<List<ApplicationState>, List<ApplicationState>> loadMOQTable(final String inputSrc,
         Map<String, Unit> unitMapping);

   IParetoQTableAccessor<List<ApplicationState>, List<ApplicationState>> loadParetoQTable(String qTableIn,
         Map<String, Unit> unitMapping);

   ISOQTableAccessor<List<ApplicationState>, List<ApplicationState>> loadSOQTable(final String inputSrc,
         Map<String, Unit> unitMapping);

   S newTransformationSolution(final S s);

   void writeQTableToDisk(final IQTableAccessor<List<ApplicationState>, List<ApplicationState>> qTable,
         final String outputSrc);

}
