package at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks;

import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.primitives.Pair;

public class CustomLoss implements ILossFunction {

   @Override
   public INDArray computeGradient(final INDArray labels, final INDArray preOutput, final IActivation activationFn,
         final INDArray mask) {
      final INDArray output = activationFn.getActivation(preOutput.dup(), true).add(1e-5); // putput without zero
      final INDArray logOut = Transforms.log(output, true); // lnpi(a|s)
      final INDArray loss = logOut.mul(labels); // A(t) * lnpi(a|s)

      final INDArray gradient = activationFn.backprop(preOutput, loss).getFirst();

      return gradient;
   }

   @Override
   public Pair<Double, INDArray> computeGradientAndScore(final INDArray labels, final INDArray preOutput,
         final IActivation activationFn, final INDArray mask, final boolean average) {
      return null;
   }

   @Override
   public double computeScore(final INDArray labels, final INDArray preOutput, final IActivation activationFn,
         final INDArray mask, final boolean average) {

      return 0;

   }

   @Override
   public INDArray computeScoreArray(final INDArray labels, final INDArray preOutput, final IActivation activationFn,
         final INDArray mask) {
      return null;
   }

   @Override
   public String name() {
      return null;
   }
}
