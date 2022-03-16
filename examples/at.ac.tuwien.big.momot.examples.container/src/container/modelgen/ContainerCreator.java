package container.modelgen;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import container.ContainerModel;
import container.ContainerPackage;

public class ContainerCreator {

   public static void main(final String[] args) {

      // EcoreUtil.equals(sClassifier, cm)

      // ContainerModel cm = null;
      // FileInputStream fis = null;
      // ObjectInputStream in = null;
      // try {
      // fis = new FileInputStream(Paths.get("model", "10S_40C.xmi").toString());
      // in = new ObjectInputStream(fis);
      // cm = (ContainerModel) in.readObject();
      // in.close();
      // } catch(final IOException ex) {
      // ex.printStackTrace();
      // } catch(final ClassNotFoundException ex) {
      // ex.printStackTrace();
      // }

      final ResourceSet resourceSet = new ResourceSetImpl();

      // register UML
      final Map packageRegistry = resourceSet.getPackageRegistry();
      packageRegistry.put(ContainerPackage.eNS_URI, ContainerPackage.eINSTANCE);

      // Register XML resource as UMLResource.Factory.Instance
      final Map extensionFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
      extensionFactoryMap.put("xmi", new XMIResourceFactoryImpl());

      final URI u = URI.createFileURI(Paths.get("model", "10S_40C.xmi").toString());
      final URI u2 = URI.createFileURI(Paths.get("model", "10S_40C_dup.xmi").toString());
      final URI u3 = URI.createFileURI(Paths.get("model", "8S_20C.xmi").toString());

      final Resource resource = resourceSet.createResource(u);
      final Resource resource2 = resourceSet.createResource(u2);
      final Resource resource3 = resourceSet.createResource(u3);

      // try to load the file into resource
      try {
         resource.load(null);
         resource2.load(null);
         resource3.load(null);
      } catch(final IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      final ContainerModel cm = (ContainerModel) resource.getContents().get(0);
      final ContainerModel cm2 = (ContainerModel) resource2.getContents().get(0);
      final ContainerModel cm3 = (ContainerModel) resource3.getContents().get(0);

      System.out.println(EcoreUtil.equals(cm, cm));
      System.out.println(EcoreUtil.equals(cm, cm2));
      System.out.println(EcoreUtil.equals(cm, cm3));
      System.out.println(EcoreUtil.equals(cm2, cm3));

      // final HenshinResourceSet rSet = new HenshinResourceSet();
      // rSet.getPackageRegistry().put(ContainerPackage.eNS_URI, ContainerPackage.eINSTANCE);
      //
      // // final List<EClassifier> c = ContainerPackage.eINSTANCE.getEClassifiers();
      // //
      // // final EClassifier ec = ContainerPackage.eINSTANCE.getEClassifier("ContainerModel");
      // final EClassifier cmClassifier = ContainerPackage.eINSTANCE.getEClassifier("ContainerModel");
      // final EClassifier cClassifier = ContainerPackage.eINSTANCE.getEClassifier("Container");
      // final EClassifier sClassifier = ContainerPackage.eINSTANCE.getEClassifier("Stack");
      //
      // final ContainerModel cm = (ContainerModel) EcoreUtil.create((EClass) cmClassifier);
      // final Map<Integer, Container> idToContainerMap = new HashMap<>();
      // try(Stream<String> lines = Files.lines(Path.of("large_instances", "in_10x10"), Charset.defaultCharset())) {
      // final List<String> lineList = lines.collect(Collectors.toList());
      //
      // for(int i = 1; i < lineList.size(); i++) {
      // final Stack curS = (Stack) EcoreUtil.create((EClass) sClassifier);
      //
      // curS.setId("S" + i);
      //
      // final String l = lineList.get(i);
      // final String[] les = l.split(" ");
      // Container prev = null;
      // for(int j = 1; j < les.length; j++) {
      // final Container curC = (Container) EcoreUtil.create((EClass) cClassifier);
      //
      // final int cNum = Integer.valueOf(les[j]);
      // curC.setId("C" + cNum);
      // curC.setOn(curS);
      // if(j > 1) {
      // curC.setOnTopOf(prev);
      // }
      // if(cNum == 1) {
      // cm.setNextContainer(curC);
      // }
      // prev = curC;
      // cm.getContainer().add(curC);
      // idToContainerMap.put(cNum, curC);
      //
      // }
      // curS.setTopContainer(prev);
      // cm.getStack().add(curS);
      // }
      // for(final Container c : cm.getContainer()) {
      //
      // final int curId = Integer.valueOf(c.getId().substring(1));
      // if(curId < cm.getContainer().size()) {
      // c.setSuccessor(idToContainerMap.get(curId + 1));
      // }
      // }
      // } catch(final IOException e) {
      // e.printStackTrace();
      // }
      //
      // final Resource oR = rSet.createResource(URI.createFileURI("large_instances/in_10x10.xmi"));
      // oR.getContents().add(cm);
      // try {
      // oR.save(null);
      // } catch(final IOException e) {
      // e.printStackTrace();
      // }
   }

}
