package container.modelgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import container.ContainerModel;
import container.ContainerPackage;

public class FilterDuplicateModels {

   public static String PATH1 = "rand_gen_models/test_5_3S8C";
   public static String PATH2 = "rand_gen_models/train2_3S8C";

   public static void main(final String[] args) {
      final ResourceSet resourceSet = new ResourceSetImpl();
      final Map packageRegistry = resourceSet.getPackageRegistry();
      packageRegistry.put(ContainerPackage.eNS_URI, ContainerPackage.eINSTANCE);
      final Map extensionFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
      extensionFactoryMap.put("xmi", new XMIResourceFactoryImpl());

      final EClassifier cmClassifier = ContainerPackage.eINSTANCE.getEClassifier("ContainerModel");
      final EClassifier cClassifier = ContainerPackage.eINSTANCE.getEClassifier("Container");
      final EClassifier sClassifier = ContainerPackage.eINSTANCE.getEClassifier("Stack");

      final List<ContainerModel> cmList1 = new ArrayList<>();
      final List<ContainerModel> cmList2 = new ArrayList<>();

      final File[] path1Files = new File(PATH1).listFiles();
      final File[] path2Files = new File(PATH2).listFiles();

      for(final File p : path1Files) {
         final URI u = URI.createFileURI(p.getAbsolutePath());
         final Resource resource = resourceSet.createResource(u);
         try {
            resource.load(null);
         } catch(final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

         final ContainerModel cm = (ContainerModel) resource.getContents().get(0);
         cmList1.add(cm);
      }

      for(final File p : path2Files) {
         final URI u = URI.createFileURI(p.getAbsolutePath());
         final Resource resource = resourceSet.createResource(u);
         try {
            resource.load(null);
         } catch(final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

         final ContainerModel cm = (ContainerModel) resource.getContents().get(0);
         cmList2.add(cm);
      }

      for(int i = 0; i < cmList1.size(); i++) {
         for(int j = 0; j < cmList2.size(); j++) {
            if(EcoreUtil.equals(cmList1.get(i), cmList2.get(j))) {
               System.out.println(String.format("%s == %s", path1Files[i], path2Files[j]));
            }
         }
      }

   }

}
