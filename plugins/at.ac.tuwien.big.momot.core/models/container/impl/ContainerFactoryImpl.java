/**
 */
package container.impl;

import container.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ContainerFactoryImpl extends EFactoryImpl implements ContainerFactory {
   /**
    * Creates the default factory implementation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public static ContainerFactory init() {
      try {
         ContainerFactory theContainerFactory = (ContainerFactory)EPackage.Registry.INSTANCE.getEFactory(ContainerPackage.eNS_URI);
         if (theContainerFactory != null) {
            return theContainerFactory;
         }
      }
      catch (Exception exception) {
         EcorePlugin.INSTANCE.log(exception);
      }
      return new ContainerFactoryImpl();
   }

   /**
    * Creates an instance of the factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public ContainerFactoryImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public EObject create(EClass eClass) {
      switch (eClass.getClassifierID()) {
         case ContainerPackage.CONTAINER_MODEL: return createContainerModel();
         case ContainerPackage.STACK: return createStack();
         case ContainerPackage.CONTAINER: return createContainer();
         default:
            throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public ContainerModel createContainerModel() {
      ContainerModelImpl containerModel = new ContainerModelImpl();
      return containerModel;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Stack createStack() {
      StackImpl stack = new StackImpl();
      return stack;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public container.Container createContainer() {
      ContainerImpl container = new ContainerImpl();
      return container;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public ContainerPackage getContainerPackage() {
      return (ContainerPackage)getEPackage();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @deprecated
    * @generated
    */
   @Deprecated
   public static ContainerPackage getPackage() {
      return ContainerPackage.eINSTANCE;
   }

} //ContainerFactoryImpl
