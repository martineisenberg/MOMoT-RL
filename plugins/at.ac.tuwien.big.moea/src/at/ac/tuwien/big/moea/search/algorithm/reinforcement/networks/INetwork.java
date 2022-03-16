package at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks;

import java.util.ArrayList;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface INetwork {
   void fit(final ArrayList<INDArray[]> stateArrs, final ArrayList<Integer> action, final ArrayList<INDArray> reward);

   INDArray outputSingle(INDArray[] oldObs);

   void saveFinalModel();

   void saveModel(final int nrOfEpochs);
}
