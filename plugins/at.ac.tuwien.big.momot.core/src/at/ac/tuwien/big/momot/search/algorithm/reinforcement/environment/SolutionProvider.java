package at.ac.tuwien.big.momot.search.algorithm.reinforcement.environment;

import at.ac.tuwien.big.moea.search.algorithm.local.INeighborhood;
import at.ac.tuwien.big.moea.search.algorithm.local.neighborhood.AbstractNeighborhoodFunction;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain.IEncodingStrategy;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.environment.ISolutionExtender;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.search.algorithm.local.neighborhood.AbstractMatchSolutionNeighborhood;
import at.ac.tuwien.big.momot.search.algorithm.local.neighborhood.AbstractTransformationSolutionStepper;
import at.ac.tuwien.big.momot.search.solution.executor.SearchHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Solution;
import org.nd4j.linalg.api.ndarray.INDArray;

public class SolutionProvider<S extends Solution> extends AbstractNeighborhoodFunction<TransformationSolution>
      implements ISolutionExtender<TransformationSolution> {
   private static final int DEFAULT_MAX_NEIGHBORS = 50;
   private final SearchHelper searchHelper;

   public SolutionProvider(final SearchHelper searchHelper) {
      this(searchHelper, DEFAULT_MAX_NEIGHBORS);
   }

   public SolutionProvider(final SearchHelper searchHelper, final int maxNeighbors) {
      super(maxNeighbors);
      this.searchHelper = searchHelper;
   }

   @Override
   public Object[] generateExtendedSolution(final IEncodingStrategy<TransformationSolution> encoder,
         final TransformationSolution solution, final INDArray distribution) {
      final TransformationSolution nonEmptySolution = TransformationSolution
            .removePlaceholdersKeepUnitApplicationAssignment(solution);
      return getSearchHelper().appendParticularVariable(encoder, nonEmptySolution, distribution);
   }

   @Override
   public TransformationSolution generateExtendedSolution(final TransformationSolution solution,
         final List<ApplicationState> assignments) {
      final TransformationSolution extendedSolution = TransformationSolution
            .removePlaceholdersKeepUnitApplicationAssignment(getSearchHelper().appendVariables(solution, assignments));
      return extendedSolution;
   }

   @Override
   public INeighborhood<TransformationSolution> generateNeighbors(final TransformationSolution solution,
         final int maxNeighbors) {
      final TransformationSolution nonEmptySolution = TransformationSolution.removePlaceholders(solution);
      return new AbstractMatchSolutionNeighborhood(nonEmptySolution, maxNeighbors) {

         @Override
         public Iterator<TransformationSolution> iterator() {
            return new AbstractTransformationSolutionStepper(getBaseSolution(), getMaxNeighbors()) {
               private TransformationSolution extendSolution(final TransformationSolution baseSolution) {
                  return getSearchHelper().appendRandomVariables(baseSolution, 1);

               }

               @Override
               protected TransformationSolution getNext() {

                  final TransformationSolution solution = TransformationSolution
                        .removePlaceholdersKeepUnitApplicationAssignment(extendSolution(getBaseSolution()));

                  return solution;
               }
            };
         }
      };
   }

   @Override
   public INeighborhood<TransformationSolution> generateNeighbors(final TransformationSolution solution,
         final int maxNeighbors, final IEncodingStrategy<TransformationSolution> encoder) {
      final TransformationSolution nonEmptySolution = TransformationSolution.removePlaceholders(solution);
      return new AbstractMatchSolutionNeighborhood(nonEmptySolution, maxNeighbors, encoder) {

         @Override
         public Iterator<TransformationSolution> iterator() {
            return new AbstractTransformationSolutionStepper(getBaseSolution(), getMaxNeighbors()) {
               private TransformationSolution extendSolution(final TransformationSolution baseSolution) {
                  return getSearchHelper().appendRandomVariables(baseSolution, 1);

               }

               private TransformationSolution extendSolutionWithEncoder(
                     final IEncodingStrategy<TransformationSolution> encoder,
                     final TransformationSolution baseSolution) {
                  return getSearchHelper().appendRandomVariablesWithStrategy(encoder,
                        TransformationSolution.removePlaceholders(baseSolution), 1);

               }

               @Override
               protected TransformationSolution getNext() {
                  if(encoder != null) {
                     return TransformationSolution.removePlaceholdersKeepUnitApplicationAssignment(
                           extendSolutionWithEncoder(encoder, getBaseSolution()));
                  }
                  final TransformationSolution solution = TransformationSolution
                        .removePlaceholdersKeepUnitApplicationAssignment(extendSolution(getBaseSolution()));

                  return solution;
               }
            };
         }
      };
   }

   public SearchHelper getSearchHelper() {
      return searchHelper;
   }

   @Override
   public Map<String, Unit> getUnitMapping() {
      return this.getSearchHelper().getModuleManager().getNameToUnits();
   }

}
