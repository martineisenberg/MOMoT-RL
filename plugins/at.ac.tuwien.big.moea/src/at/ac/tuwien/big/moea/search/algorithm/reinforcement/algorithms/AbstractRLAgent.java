package at.ac.tuwien.big.moea.search.algorithm.reinforcement.algorithms;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;

/**
 *
 * commonalities from different RL agent classes, e.g., network-based (AbstractNetworkAgent) and tabular
 * (AbstractTabularAgent)
 */
public abstract class AbstractRLAgent<S extends Solution> extends AbstractAlgorithm {

   protected final String scoreSavePath;
   protected final int terminateAfterEpisodes;
   protected final int saveInterval;
   protected final boolean verbose;
   protected int nrOfEpochs;
   protected int maxSolutionLength;
   protected NondominatedPopulation population;
   protected final List<Double> framesList;

   protected AbstractRLAgent(final Problem problem, final IEnvironment<S> env, final String scoreSavePath,
         final int terminateAfterEpisodes, final int saveInterval, final boolean verbose) {
      super(problem);
      this.scoreSavePath = scoreSavePath;
      this.terminateAfterEpisodes = terminateAfterEpisodes;
      this.saveInterval = saveInterval;
      this.verbose = verbose;
      this.maxSolutionLength = problem.getNumberOfVariables();
      this.nrOfEpochs = 0;
      this.framesList = new ArrayList<>();
      this.population = new NondominatedPopulation();

      java.lang.reflect.Method evaluateFunc = null;
      try {
         evaluateFunc = this.getClass().getMethod("evaluate", Solution.class);
      } catch(NoSuchMethodException | SecurityException e) {
         e.printStackTrace();
      }

      env.setEvaluationMethod(this, evaluateFunc);

   }

   protected void addSolutionIfImprovement(final Solution s) {
      if(!isDominatedByAnySolutionInParetoFront(s)) {
         this.population.add(s);
      }
   }

   protected boolean isDominatedByAnySolutionInParetoFront(final Solution s) {
      final DominanceComparator comparator = this.population.getComparator();
      for(int i = 0; i < this.population.size(); i++) {
         // if solution in pareto front dominates solution s, return true
         if(comparator.compare(s, this.population.get(i)) > 0) {
            return true;
         }
      }
      return false;
   }

   protected void printIfVerboseMode(final String str) {
      if(this.verbose) {
         System.out.println(str);
      }
   }

}
