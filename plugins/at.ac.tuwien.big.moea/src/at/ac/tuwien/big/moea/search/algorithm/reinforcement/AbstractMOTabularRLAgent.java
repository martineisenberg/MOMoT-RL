package at.ac.tuwien.big.moea.search.algorithm.reinforcement;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.IMOQTableAccessor;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IMOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.FileManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public abstract class AbstractMOTabularRLAgent<S extends Solution> extends AbstractTabularRLAgent<S> {

   protected IMOEnvironment<S> environment;

   protected IMOQTableAccessor<List<ApplicationState>, List<ApplicationState>> qTable;

   protected final List<List<Double>> rewardEarnedLists;
   protected final List<List<Double>> meanRewardEarnedLists;
   protected List<Double> cumRewardList;

   public AbstractMOTabularRLAgent(final Problem problem, final IMOEnvironment<S> environment, final String savePath,
         final int recordInterval, final int terminateAfterEpisodes, final String qTableIn, final String qTableOut,
         final boolean verbose) {
      super(problem, environment, savePath, recordInterval, terminateAfterEpisodes, qTableIn, qTableOut, verbose);

      this.rewardEarnedLists = new ArrayList<>();
      this.meanRewardEarnedLists = new ArrayList<>();
      this.environment = environment;
      this.cumRewardList = new ArrayList<>(Collections.nCopies(problem.getNumberOfObjectives(), 0.0));

      if(this.qTableIn != null) {
         this.qTable = this.utils.loadMOQTable(qTableIn, environment.getUnitMapping());
      } else {
         this.qTable = this.utils.initMOQTable(environment.getUnitMapping());
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

   public void saveRewards(final String scoreSavePath, final List<Double> framesList, final List<String> funcNames,
         final List<List<Double>> rewardLists, final List<Double> secondsPassed,
         final List<List<Double>> meanRewardLists) {
      final ArrayList<ArrayList<Double>> lll = new ArrayList<>();
      lll.add((ArrayList<Double>) framesList);

      for(final List<Double> objRewardList : rewardLists) {
         lll.add((ArrayList<Double>) objRewardList);
      }

      for(final List<Double> objMeanRewardList : meanRewardLists) {
         lll.add((ArrayList<Double>) objMeanRewardList);
      }

      lll.add((ArrayList<Double>) secondsPassed);

      FileManager.saveBenchMark("evaluations;" + String.join(";", funcNames) + ";runtime in ms;", lll,
            scoreSavePath + "_" + FileManager.milliSecondsToFormattedDate(this.startTime) + ".csv");
   }

}
