package container.modelgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

import container.Container;
import container.ContainerModel;
import container.ContainerPackage;
import container.Stack;

public class ContainerRandCreator {
   private final static int NO_STACKS = 3;
   private final static int NO_CONTAINERS = 8;
   private final static int NO_INSTANCES_TO_GEN = 30;
   private final static Random RNG = new Random();
   private final static boolean VERBOSE = true;

   public static void main(final String[] args) {
      final HenshinResourceSet rSet = new HenshinResourceSet();
      rSet.getPackageRegistry().put(ContainerPackage.eNS_URI, ContainerPackage.eINSTANCE);

      // final List<EClassifier> c = ContainerPackage.eINSTANCE.getEClassifiers();
      //
      // final EClassifier ec = ContainerPackage.eINSTANCE.getEClassifier("ContainerModel");
      final EClassifier cmClassifier = ContainerPackage.eINSTANCE.getEClassifier("ContainerModel");
      final EClassifier cClassifier = ContainerPackage.eINSTANCE.getEClassifier("Container");
      final EClassifier sClassifier = ContainerPackage.eINSTANCE.getEClassifier("Stack");

      final List<ContainerModel> cmList = new ArrayList<>();
      while(cmList.size() < NO_INSTANCES_TO_GEN) {

         final ContainerModel cm = (ContainerModel) EcoreUtil.create((EClass) cmClassifier);

         final List<Stack> stackList = new ArrayList<>();
         for(int i = 1; i <= NO_STACKS; i++) {
            final Stack curS = (Stack) EcoreUtil.create((EClass) sClassifier);
            curS.setId("S" + i);
            stackList.add(curS);
            cm.getStack().add(curS);
         }

         final List<Container> containerList = new ArrayList<>();
         for(int i = 1; i <= NO_CONTAINERS; i++) {
            final Container curC = (Container) EcoreUtil.create((EClass) cClassifier);
            curC.setId("C" + i);
            containerList.add(curC);
            cm.getContainer().add(curC);
         }

         for(int i = 0; i < containerList.size(); i++) {
            if(i < containerList.size() - 1) {
               containerList.get(i).setSuccessor(containerList.get(i + 1));
            }
         }

         cm.setNextContainer(containerList.get(0));

         for(int i = 0; i < NO_CONTAINERS; i++) {
            final Container cDistr = containerList.remove(RNG.nextInt(containerList.size()));
            final Stack curS = stackList.get(RNG.nextInt(stackList.size()));
            final Container curTop = curS.getTopContainer();
            if(curTop != null) {
               cDistr.setOnTopOf(curTop);
            }
            cDistr.setOn(curS);
            curS.setTopContainer(cDistr);
         }

         boolean createDup = false;
         for(final ContainerModel curCM : cmList) {
            if(EcoreUtil.equals(curCM, cm)) {
               System.out.println("created duplicate");
               createDup = true;
               break;
            }
         }

         final int firstOnTop = (int) cm.getStack().stream().filter(s -> s.getTopContainer() != null)
               .filter(s -> s.getTopContainer().getId().compareTo("C1") == 0).count();

         if(!createDup && firstOnTop == 0) {
            if(VERBOSE) {
               for(final Stack s : cm.getStack()) {
                  final List<String> stackContainerIds = new ArrayList<>();
                  Container c = s.getTopContainer();
                  System.out.print("\n" + s.getId() + ": ");

                  while(c != null) {

                     stackContainerIds.add(c.getId());

                     // System.out.print(c.getId() + " ");
                     c = c.getOnTopOf();
                  }
                  Collections.reverse(stackContainerIds);
                  System.out.print(String.join(" ", stackContainerIds));

               }
               System.out.println("Top:" + firstOnTop);
               System.out.print("\n");
            }

            cmList.add(cm);
            final Resource oR = rSet.createResource(URI.createFileURI(
                  "rand_gen_models/" + NO_STACKS + "S_" + NO_CONTAINERS + "C_" + (cmList.size() - 1) + ".xmi"));
            oR.getContents().add(cm);
            try {
               oR.save(null);
            } catch(final IOException e) {
               e.printStackTrace();
            }
         }

      }

   }

}
