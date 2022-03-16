package container.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import container.Container;
import container.ContainerModel;
import container.Stack;

public class ContainerUtils {

   public static double blockingFactor(final ContainerModel model) {
      double lowerIdCntBelow = 0;
      for(final Stack s : model.getStack()) {
         final int containersOnStackMinus1 = (int) model.getContainer().stream().filter(c -> c.getOn() != null)
               .filter(c -> c.getOn().getId() == s.getId()).count() - 1;
         final int normalizingConst = containersOnStackMinus1 * (containersOnStackMinus1 + 1) / 2;
         final Container c = s.getTopContainer();
         if(c != null) {
            final List<Integer> stackContainerIds = new ArrayList<>(
                  Arrays.asList(Integer.valueOf(c.getId().substring(1))));
            lowerIdCntBelow += countLowerIdContainersBelowRec(c, normalizingConst, stackContainerIds);
         }
      }
      return lowerIdCntBelow / model.getContainer().size();
   }

   public static double calculateRetrievedContainers(final ContainerModel model) {

      final int retrievedContainers = (int) model.getContainer().stream().filter(c -> c.getOn() == null).count();

      return retrievedContainers - containerIndex(model);
   }

   public static double containerIndex(final ContainerModel model) {
      double lowerIdCntBelow = 0;
      for(final Stack s : model.getStack()) {
         final int containersOnStackMinus1 = (int) model.getContainer().stream().filter(c -> c.getOn() != null)
               .filter(c -> c.getOn().getId() == s.getId()).count() - 1;
         final int normalizingConst = containersOnStackMinus1 * (containersOnStackMinus1 + 1) / 2;
         final Container c = s.getTopContainer();
         if(c != null) {
            final List<Integer> stackContainerIds = new ArrayList<>(
                  Arrays.asList(Integer.valueOf(c.getId().substring(1))));
            lowerIdCntBelow += countLowerIdContainersBelowRec(c, normalizingConst, stackContainerIds);
         }
      }
      return lowerIdCntBelow / model.getContainer().size();
   }

   private static double countLowerIdContainersBelowRec(final Container c, final int normalizingConst,
         final List<Integer> stackContainerIds) {
      if(c.getOnTopOf() == null) {
         return 0;
      }
      final Container lowerContainer = c.getOnTopOf();
      final int lowerCId = Integer.valueOf(lowerContainer.getId().substring(1));
      stackContainerIds.add(lowerCId);

      return countLowerIdContainersBelowRec(lowerContainer, normalizingConst, stackContainerIds)
            + (double) stackContainerIds.stream().filter(id -> id > lowerCId).count() / normalizingConst;
   }

   public static int noRetrievedContainers(final ContainerModel model) {
      return (int) model.getContainer().stream().filter(c -> c.getOn() == null).count();
   }

}
