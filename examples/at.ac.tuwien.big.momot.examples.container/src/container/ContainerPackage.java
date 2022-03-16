/**
 */
package container;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see container.ContainerFactory
 * @model kind="package"
 * @generated
 */
public interface ContainerPackage extends EPackage {
   /**
    * The package name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   String eNAME = "container";

   /**
    * The package namespace URI.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   String eNS_URI = "http://momot.big.tuwien.ac.at/examples/icmt/container/1.0";

   /**
    * The package namespace name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   String eNS_PREFIX = "container";

   /**
    * The singleton instance of the package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   ContainerPackage eINSTANCE = container.impl.ContainerPackageImpl.init();

   /**
    * The meta object id for the '{@link container.impl.ContainerModelImpl <em>Model</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see container.impl.ContainerModelImpl
    * @see container.impl.ContainerPackageImpl#getContainerModel()
    * @generated
    */
   int CONTAINER_MODEL = 0;

   /**
    * The feature id for the '<em><b>Stack</b></em>' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int CONTAINER_MODEL__STACK = 0;

   /**
    * The feature id for the '<em><b>Next Container</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int CONTAINER_MODEL__NEXT_CONTAINER = 1;

   /**
    * The feature id for the '<em><b>Container</b></em>' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int CONTAINER_MODEL__CONTAINER = 2;

   /**
    * The number of structural features of the '<em>Model</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int CONTAINER_MODEL_FEATURE_COUNT = 3;

   /**
    * The number of operations of the '<em>Model</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int CONTAINER_MODEL_OPERATION_COUNT = 0;

   /**
    * The meta object id for the '{@link container.impl.StackImpl <em>Stack</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see container.impl.StackImpl
    * @see container.impl.ContainerPackageImpl#getStack()
    * @generated
    */
   int STACK = 1;

   /**
    * The feature id for the '<em><b>Id</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int STACK__ID = 0;

   /**
    * The feature id for the '<em><b>Top Container</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int STACK__TOP_CONTAINER = 1;

   /**
    * The number of structural features of the '<em>Stack</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int STACK_FEATURE_COUNT = 2;

   /**
    * The number of operations of the '<em>Stack</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int STACK_OPERATION_COUNT = 0;

   /**
    * The meta object id for the '{@link container.impl.ContainerImpl <em>Container</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see container.impl.ContainerImpl
    * @see container.impl.ContainerPackageImpl#getContainer()
    * @generated
    */
   int CONTAINER = 2;

   /**
    * The feature id for the '<em><b>Id</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int CONTAINER__ID = 0;

   /**
    * The feature id for the '<em><b>On Top Of</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int CONTAINER__ON_TOP_OF = 1;

   /**
    * The feature id for the '<em><b>Successor</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int CONTAINER__SUCCESSOR = 2;

   /**
    * The feature id for the '<em><b>On</b></em>' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int CONTAINER__ON = 3;

   /**
    * The number of structural features of the '<em>Container</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int CONTAINER_FEATURE_COUNT = 4;

   /**
    * The number of operations of the '<em>Container</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int CONTAINER_OPERATION_COUNT = 0;


   /**
    * Returns the meta object for class '{@link container.ContainerModel <em>Model</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for class '<em>Model</em>'.
    * @see container.ContainerModel
    * @generated
    */
   EClass getContainerModel();

   /**
    * Returns the meta object for the containment reference list '{@link container.ContainerModel#getStack <em>Stack</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the containment reference list '<em>Stack</em>'.
    * @see container.ContainerModel#getStack()
    * @see #getContainerModel()
    * @generated
    */
   EReference getContainerModel_Stack();

   /**
    * Returns the meta object for the reference '{@link container.ContainerModel#getNextContainer <em>Next Container</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the reference '<em>Next Container</em>'.
    * @see container.ContainerModel#getNextContainer()
    * @see #getContainerModel()
    * @generated
    */
   EReference getContainerModel_NextContainer();

   /**
    * Returns the meta object for the containment reference list '{@link container.ContainerModel#getContainer <em>Container</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the containment reference list '<em>Container</em>'.
    * @see container.ContainerModel#getContainer()
    * @see #getContainerModel()
    * @generated
    */
   EReference getContainerModel_Container();

   /**
    * Returns the meta object for class '{@link container.Stack <em>Stack</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for class '<em>Stack</em>'.
    * @see container.Stack
    * @generated
    */
   EClass getStack();

   /**
    * Returns the meta object for the attribute '{@link container.Stack#getId <em>Id</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the attribute '<em>Id</em>'.
    * @see container.Stack#getId()
    * @see #getStack()
    * @generated
    */
   EAttribute getStack_Id();

   /**
    * Returns the meta object for the reference '{@link container.Stack#getTopContainer <em>Top Container</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the reference '<em>Top Container</em>'.
    * @see container.Stack#getTopContainer()
    * @see #getStack()
    * @generated
    */
   EReference getStack_TopContainer();

   /**
    * Returns the meta object for class '{@link container.Container <em>Container</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for class '<em>Container</em>'.
    * @see container.Container
    * @generated
    */
   EClass getContainer();

   /**
    * Returns the meta object for the attribute '{@link container.Container#getId <em>Id</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the attribute '<em>Id</em>'.
    * @see container.Container#getId()
    * @see #getContainer()
    * @generated
    */
   EAttribute getContainer_Id();

   /**
    * Returns the meta object for the reference '{@link container.Container#getOnTopOf <em>On Top Of</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the reference '<em>On Top Of</em>'.
    * @see container.Container#getOnTopOf()
    * @see #getContainer()
    * @generated
    */
   EReference getContainer_OnTopOf();

   /**
    * Returns the meta object for the reference '{@link container.Container#getSuccessor <em>Successor</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the reference '<em>Successor</em>'.
    * @see container.Container#getSuccessor()
    * @see #getContainer()
    * @generated
    */
   EReference getContainer_Successor();

   /**
    * Returns the meta object for the reference '{@link container.Container#getOn <em>On</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the reference '<em>On</em>'.
    * @see container.Container#getOn()
    * @see #getContainer()
    * @generated
    */
   EReference getContainer_On();

   /**
    * Returns the factory that creates the instances of the model.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the factory that creates the instances of the model.
    * @generated
    */
   ContainerFactory getContainerFactory();

   /**
    * <!-- begin-user-doc -->
    * Defines literals for the meta objects that represent
    * <ul>
    *   <li>each class,</li>
    *   <li>each feature of each class,</li>
    *   <li>each operation of each class,</li>
    *   <li>each enum,</li>
    *   <li>and each data type</li>
    * </ul>
    * <!-- end-user-doc -->
    * @generated
    */
   interface Literals {
      /**
       * The meta object literal for the '{@link container.impl.ContainerModelImpl <em>Model</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @see container.impl.ContainerModelImpl
       * @see container.impl.ContainerPackageImpl#getContainerModel()
       * @generated
       */
      EClass CONTAINER_MODEL = eINSTANCE.getContainerModel();

      /**
       * The meta object literal for the '<em><b>Stack</b></em>' containment reference list feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EReference CONTAINER_MODEL__STACK = eINSTANCE.getContainerModel_Stack();

      /**
       * The meta object literal for the '<em><b>Next Container</b></em>' reference feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EReference CONTAINER_MODEL__NEXT_CONTAINER = eINSTANCE.getContainerModel_NextContainer();

      /**
       * The meta object literal for the '<em><b>Container</b></em>' containment reference list feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EReference CONTAINER_MODEL__CONTAINER = eINSTANCE.getContainerModel_Container();

      /**
       * The meta object literal for the '{@link container.impl.StackImpl <em>Stack</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @see container.impl.StackImpl
       * @see container.impl.ContainerPackageImpl#getStack()
       * @generated
       */
      EClass STACK = eINSTANCE.getStack();

      /**
       * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EAttribute STACK__ID = eINSTANCE.getStack_Id();

      /**
       * The meta object literal for the '<em><b>Top Container</b></em>' reference feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EReference STACK__TOP_CONTAINER = eINSTANCE.getStack_TopContainer();

      /**
       * The meta object literal for the '{@link container.impl.ContainerImpl <em>Container</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @see container.impl.ContainerImpl
       * @see container.impl.ContainerPackageImpl#getContainer()
       * @generated
       */
      EClass CONTAINER = eINSTANCE.getContainer();

      /**
       * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EAttribute CONTAINER__ID = eINSTANCE.getContainer_Id();

      /**
       * The meta object literal for the '<em><b>On Top Of</b></em>' reference feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EReference CONTAINER__ON_TOP_OF = eINSTANCE.getContainer_OnTopOf();

      /**
       * The meta object literal for the '<em><b>Successor</b></em>' reference feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EReference CONTAINER__SUCCESSOR = eINSTANCE.getContainer_Successor();

      /**
       * The meta object literal for the '<em><b>On</b></em>' reference feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EReference CONTAINER__ON = eINSTANCE.getContainer_On();

   }

} //ContainerPackage
