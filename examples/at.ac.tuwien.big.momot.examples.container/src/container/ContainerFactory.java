/**
 */
package container;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see container.ContainerPackage
 * @generated
 */
public interface ContainerFactory extends EFactory {
   /**
    * The singleton instance of the factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   ContainerFactory eINSTANCE = container.impl.ContainerFactoryImpl.init();

   /**
    * Returns a new object of class '<em>Model</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return a new object of class '<em>Model</em>'.
    * @generated
    */
   ContainerModel createContainerModel();

   /**
    * Returns a new object of class '<em>Stack</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return a new object of class '<em>Stack</em>'.
    * @generated
    */
   Stack createStack();

   /**
    * Returns a new object of class '<em>Container</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return a new object of class '<em>Container</em>'.
    * @generated
    */
   Container createContainer();

   /**
    * Returns the package supported by this factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the package supported by this factory.
    * @generated
    */
   ContainerPackage getContainerPackage();

} //ContainerFactory
