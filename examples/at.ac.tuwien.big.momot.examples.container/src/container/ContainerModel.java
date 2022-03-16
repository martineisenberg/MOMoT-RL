/**
 */
package container;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link container.ContainerModel#getStack <em>Stack</em>}</li>
 *   <li>{@link container.ContainerModel#getNextContainer <em>Next Container</em>}</li>
 *   <li>{@link container.ContainerModel#getContainer <em>Container</em>}</li>
 * </ul>
 *
 * @see container.ContainerPackage#getContainerModel()
 * @model
 * @generated
 */
public interface ContainerModel extends EObject {
   /**
    * Returns the value of the '<em><b>Stack</b></em>' containment reference list.
    * The list contents are of type {@link container.Stack}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the value of the '<em>Stack</em>' containment reference list.
    * @see container.ContainerPackage#getContainerModel_Stack()
    * @model containment="true" required="true"
    * @generated
    */
   EList<Stack> getStack();

   /**
    * Returns the value of the '<em><b>Next Container</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the value of the '<em>Next Container</em>' reference.
    * @see #setNextContainer(Container)
    * @see container.ContainerPackage#getContainerModel_NextContainer()
    * @model
    * @generated
    */
   Container getNextContainer();

   /**
    * Sets the value of the '{@link container.ContainerModel#getNextContainer <em>Next Container</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Next Container</em>' reference.
    * @see #getNextContainer()
    * @generated
    */
   void setNextContainer(Container value);

   /**
    * Returns the value of the '<em><b>Container</b></em>' containment reference list.
    * The list contents are of type {@link container.Container}.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the value of the '<em>Container</em>' containment reference list.
    * @see container.ContainerPackage#getContainerModel_Container()
    * @model containment="true" required="true"
    * @generated
    */
   EList<Container> getContainer();

} // ContainerModel
