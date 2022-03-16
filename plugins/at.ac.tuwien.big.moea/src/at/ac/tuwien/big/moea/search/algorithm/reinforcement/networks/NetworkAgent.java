package at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.UnitParameter;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.ISOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.SOEnvResponse;

import java.util.ArrayList;
import java.util.Arrays;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class NetworkAgent<S extends Solution> extends AbstractNetworkAgent<S> {

   private final INetwork nn;

   public NetworkAgent(final INetwork nn, final Problem problem, final ISOEnvironment<S> environment,
         final String scoreSavePath, final int epochsPerModelSave, final int terminateAfterEpisodes,
         final boolean verbose) {
      super(problem, environment, scoreSavePath, terminateAfterEpisodes, epochsPerModelSave, verbose);
      this.nn = nn;

   }

   @Override
   protected void iterate() {
      if(this.startTime == 0) {
         this.startTime = System.currentTimeMillis();
      }

      this.trainEpoch();

      if(saveInterval > 0 && nrOfEpochs % saveInterval == 0) {
         nn.saveModel(nrOfEpochs);
      }

      if(terminateAfterEpisodes > 0 && terminateAfterEpisodes <= nrOfEpochs) {
         this.terminate();
         System.out.println("Terminated after " + nrOfEpochs + " epochs");
         nn.saveFinalModel();
      }

      if(this.scoreSavePath != null && saveInterval > 0 && nrOfEpochs % saveInterval == 0) {
         System.out.println("Saving rewards at epoch " + nrOfEpochs + " after "
               + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
         saveRewards(framesList, rewardEarned, meanRewardEarned, timePassedList, nrOfEpochs);
      }

   }

   public void trainEpoch() {

      double cumReward = 0;
      int epochSteps = 1;
      final boolean done = false;
      // double oldReward = 2.82842;

      INDArray dist;
      INDArray[] oldObs = this.encoder.encodeSolution(environment.reset());
      S oldSolution = environment.getInitialSolution();

      final ArrayList<INDArray[]> stateLs = new ArrayList<>();
      final ArrayList<Integer> actionLs = new ArrayList<>();
      final ArrayList<INDArray> rewardLs = new ArrayList<>();

      printIfVerboseMode("Starting training epoch " + nrOfEpochs++ + "..");

      while(!done) {
         if(this.verbose) {
            this.encoder.printModel(oldSolution);
         }
         dist = nn.outputSingle(oldObs);

         printIfVerboseMode(this.encoder.getDistrActionProbabilities(dist.dup(), oldSolution, true));

         final SOEnvResponse<S> response = environment.step(oldSolution, dist.dup());

         final int action = response.getAppliedActionId();

         final S newSolution = response.getState();

         this.addSolutionIfImprovement(newSolution);

         stateLs.add(oldObs);
         actionLs.add(action);

         final double[] curReward = new double[1];

         curReward[0] = response.getReward();

         final UnitParameter up = this.encoder.getFixedUnitApplicationStrategy(oldSolution, action)
               .getDistributionSampleRule();
         printIfVerboseMode("\nAction => "
               + String.format("%d (%s -> %s)", action, up.getUnitName(), up.getParameterValues().toString()));
         printIfVerboseMode("Reward =>=> " + Arrays.toString(curReward));

         rewardLs.add(Nd4j.create(curReward));

         cumReward += curReward[0];

         framecount++;

         if(response.getDoneStatus() != null) {

            environment.reset();
            break;
         }

         oldObs = this.encoder.encodeSolution(newSolution);
         oldSolution = newSolution;
         epochSteps++;
      }

      nn.fit(stateLs, actionLs, rewardLs);

      rewardEarned.add(cumReward);
      framesList.add((double) framecount);
      timePassedList.add((double) (System.currentTimeMillis() - startTime));
      meanRewardEarned.add(cumReward / epochSteps);
      printIfVerboseMode("Avg. reward: " + cumReward / epochSteps);

   }

}
