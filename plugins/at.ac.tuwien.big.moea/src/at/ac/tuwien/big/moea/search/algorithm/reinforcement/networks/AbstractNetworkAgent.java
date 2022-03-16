package at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.algorithms.AbstractRLAgent;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IEncodingStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.ISOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.FileManager;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public abstract class AbstractNetworkAgent<S extends Solution> extends AbstractRLAgent<S> {

   int framecount;
   protected final ArrayList<Double> rewardEarned;
   protected final ArrayList<Double> framesList;
   protected final ArrayList<Double> timePassedList;
   protected final ArrayList<Double> meanRewardEarned;

   protected long startTime;
   protected ISOEnvironment<S> environment;

   protected IEncodingStrategy<S> encoder;

   public AbstractNetworkAgent(final Problem problem, final ISOEnvironment<S> environment, final String scoreSavePath,
         final int terminateAfterEpisodes, final int epochsPerSave, final boolean verbose) {
      super(problem, environment, scoreSavePath, terminateAfterEpisodes, epochsPerSave, verbose);

      FileManager.createDirsIfNonNullAndNotExists(scoreSavePath);

      this.encoder = environment.getProblemEncoder();

      this.environment = environment;
      this.rewardEarned = new ArrayList<>();
      this.framesList = new ArrayList<>();
      this.timePassedList = new ArrayList<>();
      this.meanRewardEarned = new ArrayList<>();
      this.framecount = 0;
   }

   @Override
   public NondominatedPopulation getResult() {
      return this.population;
   }

   public void saveRewards(final List<Double> framesList, final List<Double> rewardList,
         final List<Double> meanRewardList, final List<Double> timePassedList, final long ts) {
      final ArrayList<ArrayList<Double>> lll = new ArrayList<>();
      lll.add((ArrayList<Double>) framesList);
      lll.add((ArrayList<Double>) rewardList);
      lll.add((ArrayList<Double>) meanRewardList);
      lll.add((ArrayList<Double>) timePassedList);

      FileManager.saveBenchMark("evaluations;reward;averageReward;runtime in ms;", lll,
            scoreSavePath + "_" + FileManager.milliSecondsToFormattedDate(startTime) + ".csv");
   }
}
