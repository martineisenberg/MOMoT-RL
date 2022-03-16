package at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain.strategies;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.FixedRuleApplicationStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.UnitParameter;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks.CustomLoss;
import at.ac.tuwien.big.momot.examples.stack.stack.Stack;
import at.ac.tuwien.big.momot.examples.stack.stack.StackModel;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.domain.AbstractEncodingStrategy;
import at.ac.tuwien.big.momot.util.MomotUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

public class StackEncodingStrategy<S extends Solution> extends AbstractEncodingStrategy<S> {

   @Override
   public List<UnitParameter> createBaseRules() {
      final List<UnitParameter> baseRules = new ArrayList<>();
      final Map<String, Object> parameterValues = new HashMap<>();

      baseRules.add(new UnitParameter("stack::Stack::shiftLeft", parameterValues));
      baseRules.add(new UnitParameter("stack::Stack::shiftRight", parameterValues));

      return baseRules;
   }

   @Override
   public List<UnitParameter> createPostBaseRules() {
      return null;
   }

   @Override
   public INDArray[] encodeSolution(final S s) {
      final StackModel sm = MomotUtil.getRoot(((TransformationSolution) s).execute(), StackModel.class);
      final INDArray[] encodingsArr = new INDArray[1];

      final INDArray encoding = Nd4j.zeros(1, sm.getStacks().size());

      int col = 0;
      for(final Stack stack : sm.getStacks()) {
         encoding.putScalar(0, col++, stack.getLoad());
      }

      encodingsArr[0] = encoding;

      return encodingsArr;
   }

   private boolean foundPerfectDistribution(final S s) {
      final TransformationSolution solution = (TransformationSolution) s;
      final StackModel sm = MomotUtil.getRoot(solution.getResultGraph(), StackModel.class);
      final double std = sm.getStacks().stream().mapToDouble(x -> x.getLoad()).average().getAsDouble();
      return std == 0.0;
   }

   @Override
   public int getActionSpace(final S initialSolution) {
      final TransformationSolution solution = (TransformationSolution) initialSolution;
      final StackModel sm = MomotUtil.getRoot(solution.getSourceGraph(), StackModel.class);
      // Per stack 10 possibilities, shiftLeft or shiftRight with shift amount in [1,5]
      return sm.getStacks().size() * 2 * 5;
   }

   @Override
   public ComputationGraph getActorArchitecture(final S solution, final double lr, final double l2Regularization,
         final double dropoutRate, final boolean printArchitecture) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ComputationGraph getCriticArchitecture(final S solution, final double lr, final double l2Regularization,
         final double dropoutRate, final boolean printArchitecture) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getDistrActionProbabilities(INDArray dist, final S solution, final boolean renormalizeIllegalActions) {
      if(renormalizeIllegalActions) {
         final StackModel sm = MomotUtil.getRoot(((TransformationSolution) solution).getResultGraph(),
               StackModel.class);

         final Map<String, Integer> stackIdToLoad = sm.getStacks().stream()
               .collect(Collectors.toMap(s -> s.getId(), s -> s.getLoad()));

         for(int i = 0; i < this.getActionSpace(solution); i++) {
            final int shiftAmount = i % 5 + 1;
            final int shiftFromId = i / 10 + 1;
            if(shiftAmount > stackIdToLoad.get("Stack_" + shiftFromId)) {
               dist.putScalar(i, 0.0);
            }
         }
         dist = dist.div(dist.sum(1));
      }

      final StringBuilder sb = new StringBuilder();
      final double[] dDist = dist.toDoubleVector();
      for(int i = 0; i < dDist.length; i++) {
         final int shiftAmount = i % 5 + 1;
         final int shiftFromId = i / 10 + 1;
         String unitChar = "";
         if((i / 5 + 1) % 2 == 0) {
            unitChar = "L";
         } else {
            unitChar = "R";
         }
         sb.append(String.format("S%d: %s%d => %.3f\t", shiftFromId, unitChar, shiftAmount, dDist[i]));
         if(i > 0 && i % 5 == 0) {
            sb.append("\n");
         }
      }
      return sb.toString();
   }

   @Override
   public List<String> getEpisodeEndingRules() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public FixedRuleApplicationStrategy getFixedUnitApplicationStrategy(final S s, final int action) {
      final FixedRuleApplicationStrategy applicationStrategy = new FixedRuleApplicationStrategy();
      String unitName = null;
      final Map<String, Object> parameterValues = new HashMap<>();
      UnitParameter moveAction = null;

      final int shiftAmount = action % 5 + 1;
      final int shiftFromId = action / 10 + 1;

      final TransformationSolution solution = (TransformationSolution) s;
      final StackModel sm = MomotUtil.getRoot(solution.execute(), StackModel.class);

      final int noStacks = sm.getStacks().size();
      if((action / 5 + 1) % 2 == 0) {
         unitName = "stack::Stack::shiftLeft";
      } else {
         unitName = "stack::Stack::shiftRight";
      }

      parameterValues.put("fromId", String.format("Stack_%d", shiftFromId));
      parameterValues.put("amount", shiftAmount);

      moveAction = new UnitParameter(unitName, parameterValues);
      applicationStrategy.setDistributionSampleRule(moveAction);

      return applicationStrategy;
   }

   @Override
   public ComputationGraph getNetworkArchitecture(final S solution, final double lr, final double l2Regularization,
         final double dropoutRate, final boolean printArchitecture) {
      final ComputationGraphConfiguration.GraphBuilder confBuild = new NeuralNetConfiguration.Builder().seed(123)
            .updater(new Adam(lr)).weightInit(WeightInit.NORMAL).l2(l2Regularization).dropOut(1 - dropoutRate)
            .graphBuilder().addInputs("disc_in");

      final InputType[] outputTypes = new InputType[1];
      outputTypes[0] = InputType.feedForward(this.getStateSpace(solution));

      confBuild.addLayer("policy_1", new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(512).build(),
            "disc_in");
      confBuild.addLayer("policy_2", new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(256).build(),
            "policy_1");

      confBuild.addLayer("action", new OutputLayer.Builder(new CustomLoss()).nOut(this.getActionSpace(solution))
            .activation(Activation.SOFTMAX).build(), "policy_2");

      confBuild.setOutputs("action").setInputTypes(outputTypes).build();

      final ComputationGraph net = new ComputationGraph(confBuild.build());
      net.init();

      return net;
   }

   @Override
   public int getStateSpace(final S s) {
      final TransformationSolution solution = (TransformationSolution) s;
      final StackModel sm = MomotUtil.getRoot(solution.getSourceGraph(), StackModel.class);
      return sm.getStacks().size();
   }

   @Override
   public boolean isEpisodeEndingState(final S s) {
      return this.foundPerfectDistribution(s);
   }

   @Override
   public boolean isFinalModel(final S s) {
      return this.foundPerfectDistribution(s);
   }

   @Override
   public void printModel(final S solution) {
      final StackModel sm = MomotUtil.getRoot(((TransformationSolution) solution).getResultGraph(), StackModel.class);
      final double[] loads = sm.getStacks().stream().mapToDouble(x -> x.getLoad()).toArray();
      for(final double load : loads) {
         System.out.print((int) load + "  ");
      }
      System.out.println();
   }

}
