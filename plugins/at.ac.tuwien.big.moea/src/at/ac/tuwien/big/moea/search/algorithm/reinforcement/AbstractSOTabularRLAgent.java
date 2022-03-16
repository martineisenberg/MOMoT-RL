package at.ac.tuwien.big.moea.search.algorithm.reinforcement;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ISOQTableAccessor;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.ISOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.FileManager;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public abstract class AbstractSOTabularRLAgent<S extends Solution> extends AbstractTabularRLAgent<S> {

   protected ISOEnvironment<S> environment;

   protected ISOQTableAccessor<List<ApplicationState>, List<ApplicationState>> qTable;

   protected final ArrayList<Double> rewardEarned;
   protected final ArrayList<Double> meanRewardEarned;
   protected double cumReward;

   public AbstractSOTabularRLAgent(final Problem problem, final ISOEnvironment<S> environment, final String savePath,
         final int recordInterval, final int terminateAfterEpisodes, final String qTableIn, final String qTableOut,
         final boolean verbose) {
      super(problem, environment, savePath, recordInterval, terminateAfterEpisodes, qTableIn, qTableOut, verbose);

      this.cumReward = 0;
      this.environment = environment;
      this.rewardEarned = new ArrayList<>();
      this.meanRewardEarned = new ArrayList<>();

      if(this.qTableIn != null) {
         this.qTable = this.utils.loadSOQTable(qTableIn, environment.getUnitMapping());
      } else {
         this.qTable = this.utils.initSOQTable(environment.getUnitMapping());
         this.qTable.addStateIfNotExists(new ArrayList<>());
      }
   }

   @Override
   public NondominatedPopulation getResult() {
      if(this.qTableOut != null) {
         utils.writeQTableToDisk(this.qTable, this.qTableOut);
      }
      return this.population;
   }

   public void saveRewards(final String scoreSavePath, final List<Double> framesList, final List<Double> rewardList,
         final List<Double> secondsPassed, final List<Double> meanReward) {
      final ArrayList<ArrayList<Double>> lll = new ArrayList<>();
      lll.add((ArrayList<Double>) framesList);
      lll.add((ArrayList<Double>) rewardList);
      lll.add((ArrayList<Double>) meanReward);
      lll.add((ArrayList<Double>) secondsPassed);
      FileManager.saveBenchMark("evaluations;reward;averageReward;runtime in ms;", lll,
            scoreSavePath + "_" + FileManager.milliSecondsToFormattedDate(this.startTime) + ".csv");
   }

}
