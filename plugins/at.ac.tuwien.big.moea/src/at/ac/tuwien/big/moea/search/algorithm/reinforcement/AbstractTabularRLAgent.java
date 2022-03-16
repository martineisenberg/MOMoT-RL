package at.ac.tuwien.big.moea.search.algorithm.reinforcement;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.algorithms.AbstractRLAgent;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.FileManager;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IRLUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public abstract class AbstractTabularRLAgent<S extends Solution> extends AbstractRLAgent<S> {

   protected long startTime;
   protected final List<Double> framesList;
   protected final List<Double> timePassedList;
   protected S currentSolution;
   protected int iterations = 0;
   protected IRLUtils<S> utils;
   protected int epochSteps;
   protected String qTableIn;
   protected String qTableOut;
   protected Random rng = null;

   protected AbstractTabularRLAgent(final Problem problem, final IEnvironment<S> environment, final String savePath,
         final int recordInterval, final int terminateAfterEpisodes, final String qTableIn, final String qTableOut,
         final boolean verbose) {
      super(problem, environment, savePath, terminateAfterEpisodes, recordInterval, verbose);

      this.framesList = new ArrayList<>();
      this.timePassedList = new ArrayList<>();

      this.rng = new Random();

      this.startTime = 0;

      FileManager.createDirsIfNonNullAndNotExists(savePath);

      this.currentSolution = environment.reset();
      evaluate(this.currentSolution);

      this.population.add(this.currentSolution);

      this.utils = environment.getRLUtils();

      this.qTableIn = qTableIn;
      this.qTableOut = qTableOut;

   }

   public abstract List<ApplicationState> epsGreedyDecision();

}
