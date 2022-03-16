package at.ac.tuwien.big.moea.search.algorithm;

import at.ac.tuwien.big.moea.ISearchOrchestration;
import at.ac.tuwien.big.moea.search.algorithm.provider.AbstractRegisteredAlgorithm;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.algorithms.ParetoQLearning;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.algorithms.SingleObjectiveQLearning;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.algorithms.NegotiatedWLearning;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.algorithms.WeightedQLearning;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.IMOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.ISOEnvironment;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks.ActorCriticNetwork;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks.INetwork;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks.NetworkAgent;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.networks.ReinforceNetwork;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.EvaluationStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.FileManager;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.LocalSearchStrategy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.nn.transferlearning.TransferLearning;
import org.moeaframework.core.Solution;

public class RLAlgorithmFactory<S extends Solution> extends AbstractAlgorithmFactory<S> {

   protected String outputPath;
   protected S initialSolution;
   protected Map<IEnvironment.Type, IEnvironment<S>> environmentMap;

   // public RLAlgorithmFactory(final ISearchOrchestration<S> searchOrchestration, final List<String>
   // episodeEndingRules,
   // final Map<String, Double> rewardMap, final INeighborhoodFunction<S> neighborhoodFunction,
   // final IFitnessComparator<?, S> fitnessComparator) {
   // setSearchOrchestration(searchOrchestration);
   // this.environment = new Environment<>(this.getInitialSolution(), neighborhoodFunction, fitnessComparator,
   // episodeEndingRules, rewardMap, searchOrchestration.getProblem().getNumberOfVariables());
   //
   // }

   public RLAlgorithmFactory(final ISearchOrchestration<S> searchOrchestration,
         final Map<IEnvironment.Type, IEnvironment<S>> environmentMap) {
      setSearchOrchestration(searchOrchestration);
      for(final IEnvironment<S> env : environmentMap.values()) {
         env.setInitialSolution(this.getInitialSolution());
         env.setSolutionLength(searchOrchestration.getProblem().getNumberOfVariables());
      }
      this.environmentMap = environmentMap;
      this.outputPath = Paths.get("output", "rl").toString();
      final File f = new File(outputPath).getAbsoluteFile();
      if(!f.exists()) {
         f.mkdirs();
      }

   }

   public AbstractRegisteredAlgorithm<NetworkAgent<S>> createActorCritic(final double gamma, final double learningRate,
         final double l2Regularization, final double dropoutRate, final String actorNetworkPath,
         final String criticNetworkPath, final boolean disableRegularization, final String actorSavePath,
         final String criticSavePath, final String scoreSavePath, final int epochsPerModelSave,
         final boolean enableProgressServer, final int terminateAfterEpisodes, final boolean verbose) {

      if(!this.getEnvironments().containsKey(IEnvironment.Type.PolicyBasedEnvironment)) {
         throw new RuntimeException(
               "None of the environments passed to RLAlgorithmFactory is of type  \"PolicyBasedEnvironment\"!");
      }

      ComputationGraph actorNN = null;
      ComputationGraph criticNN = null;
      if(actorNetworkPath != null && criticNetworkPath != null) {
         System.out
               .println(String.format("Loading actor computation graph for retraining from %s...", actorNetworkPath));
         System.out
               .println(String.format("Loading actor computation graph for retraining from %s...", criticNetworkPath));
         try {
            actorNN = ComputationGraph.load(new File(actorNetworkPath), true);
            criticNN = ComputationGraph.load(new File(criticNetworkPath), true);

            if(disableRegularization) {
               final FineTuneConfiguration ftc = new FineTuneConfiguration.Builder().dropOut(0.0).l2(0.0).build();
               actorNN = new TransferLearning.GraphBuilder(actorNN).fineTuneConfiguration(ftc).build();
               criticNN = new TransferLearning.GraphBuilder(criticNN).fineTuneConfiguration(ftc).build();
            }

         } catch(final IOException e) {
            e.printStackTrace();
         }
      } else {
         actorNN = this.getPolicyEnvironment().getProblemEncoder().getActorArchitecture(this.getInitialSolution(),
               learningRate, l2Regularization, dropoutRate, verbose);
         criticNN = this.getPolicyEnvironment().getProblemEncoder().getCriticArchitecture(this.getInitialSolution(),
               learningRate, l2Regularization, dropoutRate, verbose);
      }

      if(actorSavePath != null && criticSavePath != null) {
         FileManager.createDirsIfNonNullAndNotExists(Paths.get(getOutputPath(), "nn", "ac", actorSavePath).toString(),
               Paths.get(getOutputPath(), "nn", "ac", criticSavePath).toString());
      }

      final INetwork networkAgent = ActorCriticNetwork.of(actorNN, criticNN,
            actorSavePath != null ? Paths.get(getOutputPath(), "nn", "ac", actorSavePath).toString() : null,
            criticSavePath != null ? Paths.get(getOutputPath(), "nn", "ac", criticSavePath).toString() : null,
            this.getPolicyEnvironment().getProblemEncoder().getActionSpace(this.getInitialSolution()), gamma,
            enableProgressServer);

      return new AbstractRegisteredAlgorithm<NetworkAgent<S>>() {
         @Override
         public NetworkAgent<S> createAlgorithm() {
            return new NetworkAgent<>(networkAgent, createProblem(), getPolicyEnvironment(),
                  scoreSavePath != null ? Paths.get(getOutputPath(), "rewards", scoreSavePath).toString() : null,
                  epochsPerModelSave, terminateAfterEpisodes, verbose);
         }
      };
   }

   public AbstractRegisteredAlgorithm<WeightedQLearning<S>> createChebyshevQLearner(final double[] w, final double tau,
         final LocalSearchStrategy localSearchStrategy, final int explorationSteps, final double gamma,
         final double eps, final boolean withEpsDecay, final double epsDecay, final double epsMinimum,
         final String savePath, final int recordInterval, final int terminateAfterEpisodes, final String qTableIn,
         final String qTableOut, final boolean verbose) {

      if(!this.getEnvironments().containsKey(IEnvironment.Type.MOValueBasedEnvironment)) {
         throw new RuntimeException(
               "None of the environments passed to RLAlgorithmFactory is of type  \"MOValueBasedEnvironment\"!");
      }

      return new AbstractRegisteredAlgorithm<WeightedQLearning<S>>() {
         @Override
         public WeightedQLearning<S> createAlgorithm() {
            return new WeightedQLearning<>(w, tau, localSearchStrategy, explorationSteps, gamma, eps, withEpsDecay,
                  epsDecay, epsMinimum, createProblem(), getMOValueEnvironment(),
                  savePath != null ? Paths.get(getOutputPath(), "rewards", savePath).toString() : null, recordInterval,
                  terminateAfterEpisodes, qTableIn, qTableOut, verbose);
         }
      };
   }

   public AbstractRegisteredAlgorithm<ParetoQLearning<S>> createParetoQLearner(
         final LocalSearchStrategy localSearchStrategy, final int explorationSteps, final double gamma,
         final EvaluationStrategy evaluationStrategy, final double eps, final boolean withEpsDecay,
         final double epsDecay, final double epsMinimum, final String savePath, final int recordInterval,
         final int terminateAfterEpisodes, final String qTableIn, final String qTableOut, final boolean verbose) {

      if(!this.getEnvironments().containsKey(IEnvironment.Type.MOValueBasedEnvironment)) {
         throw new RuntimeException(
               "None of the environments passed to RLAlgorithmFactory is of type  \"MOValueBasedEnvironment\"!");
      }

      return new AbstractRegisteredAlgorithm<ParetoQLearning<S>>() {
         @Override
         public ParetoQLearning<S> createAlgorithm() {
            return new ParetoQLearning<>(localSearchStrategy, explorationSteps, gamma, evaluationStrategy, eps,
                  withEpsDecay, epsDecay, epsMinimum, createProblem(), getMOValueEnvironment(),
                  savePath != null ? Paths.get(getOutputPath(), "rewards", savePath).toString() : null, recordInterval,
                  terminateAfterEpisodes, qTableIn, qTableOut, verbose);
         }
      };
   }

   /**
    * Policy gradient network architecture
    *
    * @param gamma
    *           .. discount factor
    * @param learningRate
    *           .. learning rate
    * @param problemType
    *           .. problem type for encodings
    * @param network
    *           .. can be given to continue training a saved model state
    * @param modelSavePath
    *           .. path to save model state every n epochs
    * @param scoreSavePath
    *           .. path to save score over time every n epochs
    * @param epochsPerModelSave
    *           .. n (number of epochs to save a model and score stats)
    * @param enableProgressServer
    *           .. should the server be enabled for training stats, i.e., gradient updates, loss, ..
    *
    * @param terminateAfterSeconds
    *           .. If > 0, the training run will terminate after the given amount of time
    * @return
    */
   public AbstractRegisteredAlgorithm<NetworkAgent<S>> createPolicyGradient(final double gamma,
         final double learningRate, final boolean withBaseline, final double l2Regularization, final double dropoutRate,
         final String networkPath, final boolean disableRegularization, final String modelSavePath,
         final String scoreSavePath, final int epochsPerModelSave, final boolean enableProgressServer,
         final int terminateAfterEpisodes, final boolean verbose) {

      if(!this.getEnvironments().containsKey(IEnvironment.Type.PolicyBasedEnvironment)) {
         throw new RuntimeException(
               "None of the environments passed to RLAlgorithmFactory is of type  \"PolicyBasedEnvironment\"!");
      }

      ComputationGraph nn = null;
      if(networkPath != null) {
         System.out.println("Loading computation graph for retraining...");
         try {
            nn = ComputationGraph.load(new File(networkPath), true);

            if(disableRegularization) {
               final FineTuneConfiguration ftc = new FineTuneConfiguration.Builder().dropOut(0.0).l2(0.0).build();
               nn = new TransferLearning.GraphBuilder(nn).fineTuneConfiguration(ftc).build();
            }
         } catch(final IOException e) {
            e.printStackTrace();
         }

      } else {
         // this.nn = getVPGNetwork(stateSpace, totalActions);

         nn = this.getPolicyEnvironment().getProblemEncoder().getNetworkArchitecture(this.getInitialSolution(),
               learningRate, l2Regularization, dropoutRate, verbose);
      }

      if(modelSavePath != null) {
         FileManager.createDirsIfNonNullAndNotExists(Paths.get(getOutputPath(), "nn", "pg", modelSavePath).toString());
      }

      final INetwork networkAgent = ReinforceNetwork.of(nn,
            modelSavePath != null ? Paths.get(getOutputPath(), "nn", "pg", modelSavePath).toString() : null,
            this.getPolicyEnvironment().getProblemEncoder().getActionSpace(this.getInitialSolution()), gamma,
            withBaseline, enableProgressServer);

      return new AbstractRegisteredAlgorithm<NetworkAgent<S>>() {

         @Override
         public NetworkAgent<S> createAlgorithm() {
            return new NetworkAgent<>(networkAgent, createProblem(), getPolicyEnvironment(),

                  scoreSavePath != null ? Paths.get(getOutputPath(), "rewards", scoreSavePath).toString() : null,
                  epochsPerModelSave, terminateAfterEpisodes, verbose);
         }
      };
   }

   /**
    * Exploring-focused Q-Learning
    *
    * @param explorationSteps
    *           .. sampling steps in exploration phase
    * @param gamma
    *           .. discount factor
    * @param eps
    *           .. epsilon / probability of entering exploration phase
    * @param withEpsDecay
    *           .. use epsilon decay
    * @param epsDecay
    *           .. epsilon decay value (subtracted from eps when entering exploration phase), if withEpsDecay is used
    * @param epsMinimum
    *           .. minimum epsilon to decay to, if withEpsDecay is used
    * @param savePath
    *           .. storage path
    * @param recordInterval
    *           .. Recording interval / number of epochs
    * @param terminateAfterSeconds
    *           .. If > 0, the training run will terminate after the given amount of time
    * @return
    */
   public AbstractRegisteredAlgorithm<SingleObjectiveQLearning<S>> createSingleObjectiveExploreQLearner(
         final int explorationSteps, final double gamma, final double eps, final boolean withEpsDecay,
         final double epsDecay, final double epsMinimum, final String savePath, final int recordInterval,
         final int terminateAfterEpisodes, final String qTableIn, final String qTableOut, final boolean verbose) {

      if(!this.getEnvironments().containsKey(IEnvironment.Type.SOValueBasedEnvironment)) {
         throw new RuntimeException(
               "None of the environments passed to RLAlgorithmFactory is of type  \"SOValueBasedEnvironment\"!");
      }

      return new AbstractRegisteredAlgorithm<SingleObjectiveQLearning<S>>() {
         @Override
         public SingleObjectiveQLearning<S> createAlgorithm() {
            return new SingleObjectiveQLearning<>(LocalSearchStrategy.GREEDY, explorationSteps, gamma, eps,
                  withEpsDecay, epsDecay, epsMinimum, createProblem(), getSOValueEnvironment(),
                  savePath != null ? Paths.get(getOutputPath(), "rewards", savePath).toString() : null, recordInterval,
                  terminateAfterEpisodes, qTableIn, qTableOut, verbose);
         }
      };
   }

   /**
    * Basic Q-Learning
    *
    * @param gamma
    *           .. discount factor
    * @param eps
    *           .. epsilon / probability of entering exploration phase
    * @param withEpsDecay
    *           .. use epsilon decay
    * @param epsDecay
    *           .. epsilon decay value (subtracted from eps when entering exploration phase), if withEpsDecay is used
    * @param epsMinimum
    *           .. minimum epsilon to decay to, if withEpsDecay is used
    * @param savePath
    *           .. storage path
    * @param recordInterval
    *           .. Recording interval / number of epochs
    * @param terminateAfterSeconds
    *           .. If > 0, the training run will terminate after the given amount of time
    * @return
    */
   public AbstractRegisteredAlgorithm<SingleObjectiveQLearning<S>> createSingleObjectiveQLearner(final double gamma,
         final double eps, final boolean withEpsDecay, final double epsDecay, final double epsMinimum,
         final String savePath, final int recordInterval, final int terminateAfterEpisodes, final String qTableIn,
         final String qTableOut, final boolean verbose) {

      if(!this.getEnvironments().containsKey(IEnvironment.Type.SOValueBasedEnvironment)) {
         throw new RuntimeException(
               "None of the environments passed to RLAlgorithmFactory is of type  \"SOValueBasedEnvironment\"!");
      }

      return new AbstractRegisteredAlgorithm<SingleObjectiveQLearning<S>>() {
         @Override
         public SingleObjectiveQLearning<S> createAlgorithm() {
            return new SingleObjectiveQLearning<>(LocalSearchStrategy.NONE, 1, gamma, eps, withEpsDecay, epsDecay,
                  epsMinimum, createProblem(), getSOValueEnvironment(),
                  savePath != null ? Paths.get(getOutputPath(), "rewards", savePath).toString() : null, recordInterval,
                  terminateAfterEpisodes, qTableIn, qTableOut, verbose);
         }
      };
   }

   public AbstractRegisteredAlgorithm<NegotiatedWLearning<S>> createTournamentQLearner(
         final LocalSearchStrategy localSearchStrategy, final int explorationSteps, final double gamma,
         final double eps, final boolean withEpsDecay, final double epsDecay, final double epsMinimum,
         final String savePath, final int recordInterval, final int terminateAfterEpisodes, final String qTableIn,
         final String qTableOut, final boolean verbose) {

      if(!this.getEnvironments().containsKey(IEnvironment.Type.MOValueBasedEnvironment)) {
         throw new RuntimeException(
               "None of the environments passed to RLAlgorithmFactory is of type  \"MOValueBasedEnvironment\"!");
      }

      return new AbstractRegisteredAlgorithm<NegotiatedWLearning<S>>() {
         @Override
         public NegotiatedWLearning<S> createAlgorithm() {
            return new NegotiatedWLearning<>(localSearchStrategy, explorationSteps, gamma, eps, withEpsDecay, epsDecay,
                  epsMinimum, createProblem(), getMOValueEnvironment(),
                  savePath != null ? Paths.get(getOutputPath(), "rewards", savePath).toString() : null, recordInterval,
                  terminateAfterEpisodes, qTableIn, qTableOut, verbose);
         }
      };
   }

   // public AbstractRegisteredAlgorithm<SingleObjectivePSQLearning<S>> createSingleObjectiveQLearner(final double
   // gamma,
   // final int resetNoImprSteps, final double eps, final boolean withEpsDecay, final double epsDecay,
   // final double epsMinimum, final String savePath, final int recordInterval, final int terminateAfterSeconds,
   // final String qTableIn, final String qTableOut) {
   //
   //
   // return new AbstractRegisteredAlgorithm<SingleObjectivePSQLearning<S>>() {
   // @Override
   // public SingleObjectivePSQLearning<S> createAlgorithm() {
   // return new SingleObjectivePSQLearning<>(gamma, resetNoImprSteps, eps, withEpsDecay, epsDecay, epsMinimum,
   // createProblem(), getEnvironment(), savePath, recordInterval, terminateAfterSeconds, qTableIn,
   // qTableOut);
   // }
   // };
   // }

   // public IDomainEnvironment<S> getDomainEnvironment() {
   // try {
   // return (IDomainEnvironment<S>) this.environment;
   // } catch(final ClassCastException e) {
   // e.printStackTrace();
   // }
   // return null;
   // }

   public Map<IEnvironment.Type, IEnvironment<S>> getEnvironments() {
      return this.environmentMap;
   }

   public S getInitialSolution() {
      if(initialSolution == null) {
         return getSearchOrchestration().createNewSolution(0);
      }
      return initialSolution;
   }

   public IMOEnvironment<S> getMOValueEnvironment() {
      return (IMOEnvironment<S>) this.getEnvironments().get(IEnvironment.Type.MOValueBasedEnvironment);
   }

   public String getOutputPath() {
      return this.outputPath;
   }

   public ISOEnvironment<S> getPolicyEnvironment() {
      return (ISOEnvironment<S>) this.getEnvironments().get(IEnvironment.Type.PolicyBasedEnvironment);
   }

   // public IGenericEnvironment<S> getGenericEnvironment() {
   // try {
   // return (IGenericEnvironment<S>) this.environment;
   // } catch(final ClassCastException e) {
   // e.printStackTrace();
   // }
   // return null;
   // }

   public ISOEnvironment<S> getSOValueEnvironment() {
      return (ISOEnvironment<S>) this.getEnvironments().get(IEnvironment.Type.SOValueBasedEnvironment);
   }

   public void setInitialSolution(final S initialSolution) {
      this.initialSolution = initialSolution;
   }
}
