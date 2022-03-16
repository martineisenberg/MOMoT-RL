package at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class ActorCriticNetwork implements INetwork {
   public static INetwork of(final ComputationGraph actorNN, final ComputationGraph criticNN,
         final String actorSavePath, final String criticSavePath, final int totalActions, final double gamma,
         final boolean enableProgressServer) {
      return new ActorCriticNetwork(actorNN, criticNN, actorSavePath, criticSavePath, totalActions, gamma,
            enableProgressServer);
   }

   final String actorSavePath;

   final String criticSavePath;

   private final ComputationGraph actorNN;
   private final ComputationGraph criticNN;

   private final double gamma;

   private final int totalActions;

   private ActorCriticNetwork(final ComputationGraph actorNN, final ComputationGraph criticNN,
         final String actorSavePath, final String criticSavePath, final int totalActions, final double gamma,
         final boolean enableProgressServer) {
      this.gamma = gamma;
      this.actorNN = actorNN;
      this.criticNN = criticNN;
      this.actorSavePath = actorSavePath;
      this.criticSavePath = criticSavePath;
      this.totalActions = totalActions;

      if(enableProgressServer) {
         enableServerForTrainingVisualization(actorNN);
      }

   }

   private INDArray calcAdvantage(final ComputationGraph critic, final ArrayList<INDArray[]> state,
         final ArrayList<Integer> action, final ArrayList<INDArray> reward) {
      final INDArray advantage = Nd4j.zeros(action.size(), 1);

      for(int i = 0; i < action.size(); i++) {

         if(i == action.size() - 1) {
            advantage.putScalar(i, reward.get(i).getDouble(0));
            continue;
         }

         final INDArray predQ = critic.outputSingle(state.get(i));
         final INDArray futQ = critic.outputSingle(state.get(i + 1));

         final double adv = reward.get(i).getDouble(0) + gamma * futQ.getDouble(0) - predQ.getDouble(0);

         advantage.putScalar(i, adv);

      }

      return advantage;
   }

   private INDArray[] calcTD(final ComputationGraph critic, final ArrayList<INDArray[]> state,
         final ArrayList<Integer> action, final ArrayList<INDArray> reward) {
      final INDArray val = Nd4j.zeros(action.size(), 1);
      final INDArray[] valArr = new INDArray[1];
      for(int i = 0; i < action.size(); i++) {

         if(i == action.size() - 1) {
            val.putScalar(i, reward.get(i).getDouble(0));
            continue;
         }

         final INDArray futQ = critic.outputSingle(state.get(i + 1));

         final double adv = reward.get(i).getDouble(0) + gamma * futQ.getDouble(0);

         val.putScalar(i, adv);

      }

      valArr[0] = val;
      return valArr;
   }

   private void enableServerForTrainingVisualization(final ComputationGraph nn) {
      // Initialize the user interface backend
      System.out.println("Start UI Server ...");
      final UIServer uiServer = UIServer.getInstance();

      // Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
      final StatsStorage statsStorage = new InMemoryStatsStorage(); // Alternative: new FileStatsStorage(File), for
                                                                    // saving and loading later

      uiServer.attach(statsStorage);

      nn.setListeners(new StatsListener(statsStorage));

      System.out.println("Training progress board active!");
   }

   @Override
   public void fit(final ArrayList<INDArray[]> stateArrs, final ArrayList<Integer> action,
         final ArrayList<INDArray> reward) {
      final INDArray[] curStateArrs = stateArrs.get(0);

      final INDArray advantage = calcAdvantage(criticNN, stateArrs, action, reward);

      final INDArray actions = Nd4j.zeros(action.size(), totalActions);
      for(int i = 0; i < action.size(); i++) {
         actions.putScalar(i, action.get(i), 1);

         if(i != 0) {
            for(int j = 0; j < curStateArrs.length; j++) {
               curStateArrs[j] = Nd4j.concat(0, curStateArrs[j], stateArrs.get(i)[j]).dup();
            }
         }

      }

      final INDArray temp = actions.mul(advantage);
      final INDArray[] tempArr = new INDArray[1];
      Arrays.fill(tempArr, temp);

      actorNN.fit(curStateArrs, tempArr);

      criticNN.fit(curStateArrs, calcTD(criticNN, stateArrs, action, reward));
   }

   @Override
   public INDArray outputSingle(final INDArray[] oldObs) {
      return actorNN.outputSingle(oldObs);
   }

   @Override
   public void saveFinalModel() {
      if(this.actorSavePath != null && this.criticSavePath != null) {

         try {
            actorNN.save(new File(actorSavePath + ".mod"));
            criticNN.save(new File(criticSavePath + ".mod"));

         } catch(final IOException e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void saveModel(final int nrOfEpochs) {
      if(this.actorSavePath != null && this.criticSavePath != null) {
         try {
            actorNN.save(new File(actorSavePath + "_" + nrOfEpochs + ".mod"));
            criticNN.save(new File(criticSavePath + "_" + nrOfEpochs + ".mod"));

         } catch(final IOException e) {
            e.printStackTrace();
         }
      }
   }

}
