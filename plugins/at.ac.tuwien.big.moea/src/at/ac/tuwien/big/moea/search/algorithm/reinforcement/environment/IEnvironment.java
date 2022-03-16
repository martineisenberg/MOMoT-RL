package at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IEncodingStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IRLUtils;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.LocalSearchStrategy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Solution;

public interface IEnvironment<S extends Solution> {
   enum Type {
      SOValueBasedEnvironment, MOValueBasedEnvironment, PolicyBasedEnvironment
   }

   S getInitialSolution();

   IEncodingStrategy<S> getProblemEncoder();

   IRLUtils<S> getRLUtils();

   Map<String, Unit> getUnitMapping();

   S reset();

   void setEvaluationMethod(final Object agentInstance, final Method evaluateFunction);

   void setInitialSolution(S s);

   void setSolutionLength(int length);

   void setSolutionProvider(ISolutionExtender<S> provider);

   EnvResponse<S> step(LocalSearchStrategy strategy, List<ApplicationState> nextAction,
         int explorationSteps);
}
