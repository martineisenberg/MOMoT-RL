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

public class ReinforceNetwork implements INetwork {

   public static INetwork of(final ComputationGraph nn, final String modelSavePath, final int totalActions,
         final double gamma, final boolean withBaseline, final boolean enableProgressServer) {
      return new ReinforceNetwork(nn, modelSavePath, totalActions, gamma, withBaseline, enableProgressServer);
   }

   private final ComputationGraph nn;
   private final boolean withBaseline;
   private final String modelSavePath;
   private final double gamma;

   private final int totalActions;

   private ReinforceNetwork(final ComputationGraph nn, final String modelSavePath, final int totalActions,
         final double gamma, final boolean withBaseline, final boolean enableProgressServer) {
      this.withBaseline = withBaseline;
      this.modelSavePath = modelSavePath;
      this.gamma = gamma;
      this.nn = nn;
      this.totalActions = totalActions;

      if(enableProgressServer) {
         enableServerForTrainingVisualization(nn);
      }

   }

   private INDArray calcValueOfState(final ArrayList<INDArray> reward) {
      final ArrayList<INDArray> calcd = new ArrayList<>();
      final INDArray out = Nd4j.zeros(reward.size(), 1);

      calcd.add(reward.get(reward.size() - 1));

      for(int i = reward.size() - 2; i >= 0; i--) {
         calcd.add(0, reward.get(i).add(calcd.get(0).mul(gamma)));
      }

      if(this.withBaseline) {
         for(int i = 0; i < calcd.size() - 1; i++) {
            out.put(i, calcd.get(i).sub(calcd.get(i + 1)));
         }
         out.put(calcd.size() - 1, calcd.get(calcd.size() - 1));
      } else {
         for(int i = 0; i < calcd.size(); i++) {
            out.put(i, calcd.get(i));
         }
      }

      out.subi(Nd4j.mean(out));

      if(Nd4j.std(out).getDouble(0) != 0.0) {
         out.divi(Nd4j.std(out));
      }
      return out;
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

      final INDArray advantage = calcValueOfState(reward);
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

      nn.fit(curStateArrs, tempArr);

   }

   @Override
   public INDArray outputSingle(final INDArray[] oldObs) {
      return this.nn.outputSingle(oldObs);
   }

   @Override
   public void saveFinalModel() {
      if(this.modelSavePath != null) {

         try {
            nn.save(new File(modelSavePath + ".mod"));
         } catch(final IOException e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void saveModel(final int nrOfEpochs) {
      if(this.modelSavePath != null) {

         try {
            nn.save(new File(modelSavePath + "_" + nrOfEpochs + ".mod"));
         } catch(final IOException e) {
            e.printStackTrace();
         }
      }

   }
}
