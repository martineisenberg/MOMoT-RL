package at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain.strategies;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.FixedRuleApplicationStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.UnitParameter;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks.CustomLoss;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain.AbstractEncodingStrategy;
import at.ac.tuwien.big.momot.util.MomotUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import container.Container;
import container.ContainerModel;
import container.Stack;

public class ContainerEncodingStrategy<S extends Solution> extends AbstractEncodingStrategy<S> {

   private static List<Integer> toContainerIds;
   private static Map<Integer, String> actionToUnitApp;

   private static List<Integer> getToContainerIds(final int noStacks) {
      if(toContainerIds == null) {
         toContainerIds = new ArrayList<>();

         for(int i = 1; i <= noStacks; i++) {
            for(int j = 1; j <= noStacks; j++) {
               if(j != i) {
                  toContainerIds.add(j);
               }
            }
         }
      }
      return toContainerIds;
   }

   @Override
   public List<UnitParameter> createBaseRules() {
      final List<UnitParameter> baseRules = new ArrayList<>();
      final Map<String, Object> parameterValues = new HashMap<>();

      baseRules.add(new UnitParameter("container::containerModule::RetrieveElseRelocate", parameterValues));

      return baseRules;
   }

   @Override
   public List<UnitParameter> createPostBaseRules() {
      final List<UnitParameter> postBaseRules = new ArrayList<>();

      return postBaseRules;
   }

   @Override
   public INDArray[] encodeSolution(final S solution) {
      final TransformationSolution ts = (TransformationSolution) solution;

      final ContainerModel model = MomotUtil.getRoot(ts.getResultGraph(), ContainerModel.class);
      final INDArray[] encodingArrs = new INDArray[model.getStack().size() + 1];

      int arrCnt = 0;
      double blocked = 0.0;

      for(final Stack s : model.getStack()) {
         final INDArray encoding = Nd4j.zeros(1, model.getContainer().size());
         int column = 0;

         final List<Integer> stackContainerIds = new ArrayList<>();
         Container c = s.getTopContainer();
         while(c != null) {
            stackContainerIds.add(Integer.valueOf(c.getId().substring(1)));

            // System.out.print(c.getId() + " ");

            c = c.getOnTopOf();
         }

         if(stackContainerIds.size() >= 2) {
            final List<Integer> passed = new ArrayList<>();
            for(final Integer k : stackContainerIds) {
               blocked += passed.stream().filter(a -> a > k).count();
               passed.add(k);
            }
         }
         // Collections.reverse(stackContainerIds);

         for(int i = 0; i < model.getContainer().size() - stackContainerIds.size(); i++) {
            encoding.put(0, column++, 0);
         }
         for(final int i : stackContainerIds) {
            encoding.put(0, column++, i);
         }
         encodingArrs[arrCnt++] = encoding;

      }

      final INDArray bayIndexEnc = Nd4j.zeros(1, 1);
      System.out.println("blocked " + blocked);
      bayIndexEnc.put(0, 0, blocked);
      encodingArrs[arrCnt] = bayIndexEnc;

      return encodingArrs;
   }

   @Override
   public int getActionSpace(final S initialSolution) {
      final ContainerModel model = MomotUtil.getRoot(((TransformationSolution) initialSolution).getSourceGraph(),
            ContainerModel.class);

      return model.getStack().size() * (model.getStack().size() - 1);
   }

   @Override
   public ComputationGraph getActorArchitecture(final S solution, final double lr, final double l2Regularization,
         final double dropoutRate, final boolean printArchitecture) {
      final ContainerModel model = MomotUtil.getRoot(((TransformationSolution) solution).getSourceGraph(),
            ContainerModel.class);

      final ComputationGraphConfiguration.GraphBuilder confBuild = new NeuralNetConfiguration.Builder().seed(123)
            .updater(new Adam(lr)).weightInit(WeightInit.XAVIER).l2(l2Regularization).dropOut(1 - dropoutRate)
            .graphBuilder();

      final int[] nHiddenDiscUnits = { 512, 256 };
      // final int[] nHiddenConUnits = {256, 128};

      final String[] hiddenDiscOutLayers = new String[model.getStack().size()];
      final InputType[] outputTypes = new InputType[model.getStack().size()];

      for(int s = 0; s < model.getStack().size(); s++) {
         String inputLayer = String.format("disc_in_%d", s + 1);

         confBuild.addInputs(inputLayer);
         for(int nh = 0; nh < nHiddenDiscUnits.length; nh++) {
            final String curLayerName = String.format("h_%d_%d", s, nh);
            confBuild.addLayer(curLayerName,
                  new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(nHiddenDiscUnits[nh]).build(),
                  inputLayer);
            inputLayer = curLayerName;
         }
         hiddenDiscOutLayers[s] = inputLayer;
         outputTypes[s] = InputType.feedForward(model.getContainer().size());
      }

      // confBuild.addLayer("con_1", new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(256).build(),
      // hiddenDiscOutLayers);

      confBuild.addLayer("con_2", new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(256).build(),
            hiddenDiscOutLayers);

      confBuild.addLayer("action", new OutputLayer.Builder(new CustomLoss()).nOut(this.getActionSpace(solution))
            .activation(Activation.SOFTMAX).build(), "con_2");

      confBuild.setOutputs("action").setInputTypes(outputTypes).build();

      final ComputationGraph net = new ComputationGraph(confBuild.build());
      net.init();

      if(printArchitecture) {
         System.out.println(net.summary());
      }

      return net;
   }

   @Override
   public ComputationGraph getCriticArchitecture(final S solution, final double lr, final double l2Regularization,
         final double dropoutRate, final boolean printArchitecture) {
      final ContainerModel model = MomotUtil.getRoot(((TransformationSolution) solution).getSourceGraph(),
            ContainerModel.class);

      final ComputationGraphConfiguration.GraphBuilder confBuild = new NeuralNetConfiguration.Builder().seed(123)
            .updater(new Adam(lr)).weightInit(WeightInit.XAVIER).l2(l2Regularization).dropOut(1 - dropoutRate)
            .graphBuilder();

      final int[] nHiddenDiscUnits = { 512, 256 };
      // final int[] nHiddenConUnits = {256, 128};

      final String[] hiddenDiscOutLayers = new String[model.getStack().size()];
      final InputType[] outputTypes = new InputType[model.getStack().size()];

      for(int s = 0; s < model.getStack().size(); s++) {
         String inputLayer = String.format("disc_in_%d", s + 1);

         confBuild.addInputs(inputLayer);
         for(int nh = 0; nh < nHiddenDiscUnits.length; nh++) {
            final String curLayerName = String.format("h_%d_%d", s, nh);
            confBuild.addLayer(curLayerName,
                  new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(nHiddenDiscUnits[nh]).build(),
                  inputLayer);
            inputLayer = curLayerName;
         }
         hiddenDiscOutLayers[s] = inputLayer;
         outputTypes[s] = InputType.feedForward(model.getContainer().size());
      }

      // confBuild.addLayer("con_1", new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(256).build(),
      // hiddenDiscOutLayers);

      confBuild.addLayer("con_2", new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(512).build(),
            hiddenDiscOutLayers);

      confBuild.addLayer("action",
            new OutputLayer.Builder(LossFunctions.LossFunction.L2).nOut(1).activation(Activation.IDENTITY).build(),
            "con_2");

      confBuild.setOutputs("action").setInputTypes(outputTypes).build();

      final ComputationGraph net = new ComputationGraph(confBuild.build());
      net.init();

      if(printArchitecture) {
         System.out.println(net.summary());
      }

      return net;
   }

   @Override
   public String getDistrActionProbabilities(INDArray dist, final S solution,
         final boolean renormalizeForIllegalActions) {

      if(ContainerEncodingStrategy.actionToUnitApp == null) {
         ContainerEncodingStrategy.actionToUnitApp = new HashMap<>();
         final ContainerModel model = MomotUtil.getRoot(((TransformationSolution) solution).getSourceGraph(),
               ContainerModel.class);
         final int noStacks = model.getStack().size();

         for(int i = 0; i < this.getActionSpace(solution); i++) {
            ContainerEncodingStrategy.actionToUnitApp.put(i, String.format("S%s -> S%s", i / (noStacks - 1) + 1,
                  ContainerEncodingStrategy.getToContainerIds(noStacks).get(i)));
         }
      }

      if(renormalizeForIllegalActions) {
         final ContainerModel model = MomotUtil.getRoot(((TransformationSolution) solution).getResultGraph(),
               ContainerModel.class);
         final int noStacks = model.getStack().size();

         // if "from" stack is empty, cannot relocate from it
         for(int i = 0; i < this.getActionSpace(solution); i++) {
            final int fromStack = i / (noStacks - 1) + 1;
            final int toStack = ContainerEncodingStrategy.getToContainerIds(noStacks).get(i);
            if(model.getStack().get(fromStack - 1).getTopContainer() == null
                  || model.getStack().get(fromStack - 1).getTopContainer() != null
                        && model.getStack().get(fromStack - 1).getTopContainer().getOnTopOf() == null
                        && model.getStack().get(toStack - 1).getTopContainer() == null) {
               dist.putScalar(i, 0.0);
            }
         }
         dist = dist.div(dist.sum(1));
      }
      final StringBuilder sb = new StringBuilder();
      final double[] dDist = dist.toDoubleVector();
      for(int i = 0; i < dDist.length; i++) {
         sb.append(String.format("%s => %.3f |", ContainerEncodingStrategy.actionToUnitApp.get(i), dDist[i]));
         if(i > 0 && i % 4 == 0) {
            sb.append("\n");
         }
      }
      return sb.toString();
   }

   @Override
   public List<String> getEpisodeEndingRules() {
      final List<String> endingRules = new ArrayList<>();

      return endingRules;
   }

   @Override
   public FixedRuleApplicationStrategy getFixedUnitApplicationStrategy(final S s, final int action) {
      final ContainerModel model = MomotUtil.getRoot(((TransformationSolution) s).getSourceGraph(),
            ContainerModel.class);
      final int noStacks = model.getStack().size();
      final FixedRuleApplicationStrategy applicationStrategy = new FixedRuleApplicationStrategy();
      UnitParameter moveAction = null;

      // 0 = up, 1 = right, 2 = down, 3 = left

      // 1. main role (move step in one direction)

      final int fromStack = action / (noStacks - 1) + 1;
      final int toStack = ContainerEncodingStrategy.getToContainerIds(noStacks).get(action);

      final String unitName = "container::containerModule::RetrieveElseRelocate";
      final Map<String, Object> parameterValues = new HashMap<>();

      parameterValues.put("from", "S" + String.valueOf(fromStack));
      parameterValues.put("to", "S" + String.valueOf(toStack));

      moveAction = new UnitParameter(unitName, parameterValues);
      applicationStrategy.setDistributionSampleRule(moveAction);

      // Subsequent optional rules
      final List<UnitParameter> optionalSubsequentRules = new ArrayList<>();

      final Map<String, Object> subseqRuleParameterValues = new HashMap<>();
      optionalSubsequentRules
            .add(new UnitParameter("container::containerModule::Retrieve", subseqRuleParameterValues, true));

      applicationStrategy.setOptionalSubsequentRules(optionalSubsequentRules);

      return applicationStrategy;

   }

   @Override
   public ComputationGraph getNetworkArchitecture(final S solution, final double lr, final double l2Regularization,
         final double dropoutRate, final boolean printArchitecture) {
      final ContainerModel model = MomotUtil.getRoot(((TransformationSolution) solution).getSourceGraph(),
            ContainerModel.class);

      final ComputationGraphConfiguration.GraphBuilder confBuild = new NeuralNetConfiguration.Builder().seed(123)
            .updater(new Adam(lr)).weightInit(WeightInit.NORMAL).l2(l2Regularization).dropOut(1 - dropoutRate)
            .graphBuilder();

      final int[] nHiddenDiscUnits = { 64 };
      // final int[] nHiddenConUnits = {256, 128};

      final String[] hiddenDiscOutLayers = new String[model.getStack().size() + 1];
      final InputType[] outputTypes = new InputType[model.getStack().size() + 1];

      for(int s = 0; s < model.getStack().size(); s++) {
         String inputLayer = String.format("disc_in_%d", s + 1);

         confBuild.addInputs(inputLayer);
         for(int nh = 0; nh < nHiddenDiscUnits.length; nh++) {
            final String curLayerName = String.format("h_%d_%d", s, nh);
            confBuild.addLayer(curLayerName,
                  new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(nHiddenDiscUnits[nh]).build(),
                  inputLayer);
            inputLayer = curLayerName;
         }
         hiddenDiscOutLayers[s] = inputLayer;
         outputTypes[s] = InputType.feedForward(model.getContainer().size());
      }

      final String blockingLayer = String.format("disc_in_%d", model.getStack().size() + 1);

      confBuild.addInputs(blockingLayer);
      outputTypes[outputTypes.length - 1] = InputType.feedForward(1);
      hiddenDiscOutLayers[hiddenDiscOutLayers.length - 1] = blockingLayer;

      confBuild.addLayer("con_1", new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(64).build(),
            hiddenDiscOutLayers);

      // confBuild.addLayer("con_2", new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(128).build(),
      // "con_1", "");

      confBuild.addLayer("action", new OutputLayer.Builder(new CustomLoss()).nOut(this.getActionSpace(solution))
            .activation(Activation.SOFTMAX).build(), "con_1");

      confBuild.setOutputs("action").setInputTypes(outputTypes).build();

      final ComputationGraph net = new ComputationGraph(confBuild.build());
      net.init();

      if(printArchitecture) {
         System.out.println(net.summary());
      }

      return net;
   }

   @Override
   public int getStateSpace(final S initialSolution) {
      final TransformationSolution solution = (TransformationSolution) initialSolution;
      final ContainerModel game = MomotUtil.getRoot(solution.execute(), ContainerModel.class);
      return game.getContainer().size() * game.getStack().size();
   }

   @Override
   public boolean isEpisodeEndingState(final S s) {
      final ContainerModel model = MomotUtil.getRoot(((TransformationSolution) s).getResultGraph(),
            ContainerModel.class);

      final int retrievedContainers = (int) model.getContainer().stream().filter(c -> c.getOn() == null).count();

      return retrievedContainers - model.getContainer().size() == 0 ? true : false;
   }

   @Override
   public boolean isFinalModel(final S s) {
      final TransformationSolution ts = (TransformationSolution) s;

      final ContainerModel model = MomotUtil.getRoot(ts.getResultGraph(), ContainerModel.class);

      final int nonNullContainers = (int) model.getContainer().stream().filter(c -> c.getOn() != null).count();

      return nonNullContainers == 0;
   }

   @Override
   public void printModel(final S solution) {
      final ContainerModel model = MomotUtil.getRoot(((TransformationSolution) solution).getResultGraph(),
            ContainerModel.class);

      for(final Stack s : model.getStack()) {
         final List<String> stackContainerIds = new ArrayList<>();
         Container c = s.getTopContainer();
         System.out.print("\n" + s.getId() + ": ");

         while(c != null) {
            stackContainerIds.add(c.getId());
            c = c.getOnTopOf();
         }
         Collections.reverse(stackContainerIds);
         System.out.print(String.join(" ", stackContainerIds));

      }
      System.out.println("\n");
   }

}
