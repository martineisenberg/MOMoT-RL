/**
 */
package container;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Stack</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link container.Stack#getId <em>Id</em>}</li>
 *   <li>{@link container.Stack#getTopContainer <em>Top Container</em>}</li>
 * </ul>
 *
 * @see container.ContainerPackage#getStack()
 * @model
 * @generated
 */
public interface Stack extends EObject {
   /**
    * Returns the value of the '<em><b>Id</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the value of the '<em>Id</em>' attribute.
    * @see #setId(String)
    * @see container.ContainerPackage#getStack_Id()
    * @model id="true"
    * @generated
    */
   String getId();

   /**
    * Sets the value of the '{@link container.Stack#getId <em>Id</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Id</em>' attribute.
    * @see #getId()
    * @generated
    */
   void setId(String value);

   /**
    * Returns the value of the '<em><b>Top Container</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the value of the '<em>Top Container</em>' reference.
    * @see #setTopContainer(Container)
    * @see container.ContainerPackage#getStack_TopContainer()
    * @model
    * @generated
    */
   Container getTopContainer();

   /**
    * Sets the value of the '{@link container.Stack#getTopContainer <em>Top Container</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Top Container</em>' reference.
    * @see #getTopContainer()
    * @generated
    */
   void setTopContainer(Container value);

} // Stack
