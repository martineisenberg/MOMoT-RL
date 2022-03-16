/**
 */
package container.impl;

import container.ContainerPackage;
import container.Stack;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link container.impl.ContainerImpl#getId <em>Id</em>}</li>
 *   <li>{@link container.impl.ContainerImpl#getOnTopOf <em>On Top Of</em>}</li>
 *   <li>{@link container.impl.ContainerImpl#getSuccessor <em>Successor</em>}</li>
 *   <li>{@link container.impl.ContainerImpl#getOn <em>On</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ContainerImpl extends MinimalEObjectImpl.Container implements container.Container {
   /**
    * The default value of the '{@link #getId() <em>Id</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getId()
    * @generated
    * @ordered
    */
   protected static final String ID_EDEFAULT = null;

   /**
    * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getId()
    * @generated
    * @ordered
    */
   protected String id = ID_EDEFAULT;

   /**
    * The cached value of the '{@link #getOnTopOf() <em>On Top Of</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getOnTopOf()
    * @generated
    * @ordered
    */
   protected container.Container onTopOf;

   /**
    * The cached value of the '{@link #getSuccessor() <em>Successor</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getSuccessor()
    * @generated
    * @ordered
    */
   protected container.Container successor;

   /**
    * The cached value of the '{@link #getOn() <em>On</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getOn()
    * @generated
    * @ordered
    */
   protected Stack on;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected ContainerImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return ContainerPackage.Literals.CONTAINER;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public String getId() {
      return id;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void setId(String newId) {
      String oldId = id;
      id = newId;
      if (eNotificationRequired())
         eNotify(new ENotificationImpl(this, Notification.SET, ContainerPackage.CONTAINER__ID, oldId, id));
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public container.Container getOnTopOf() {
      if (onTopOf != null && onTopOf.eIsProxy()) {
         InternalEObject oldOnTopOf = (InternalEObject)onTopOf;
         onTopOf = (container.Container)eResolveProxy(oldOnTopOf);
         if (onTopOf != oldOnTopOf) {
            if (eNotificationRequired())
               eNotify(new ENotificationImpl(this, Notification.RESOLVE, ContainerPackage.CONTAINER__ON_TOP_OF, oldOnTopOf, onTopOf));
         }
      }
      return onTopOf;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public container.Container basicGetOnTopOf() {
      return onTopOf;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void setOnTopOf(container.Container newOnTopOf) {
      container.Container oldOnTopOf = onTopOf;
      onTopOf = newOnTopOf;
      if (eNotificationRequired())
         eNotify(new ENotificationImpl(this, Notification.SET, ContainerPackage.CONTAINER__ON_TOP_OF, oldOnTopOf, onTopOf));
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public container.Container getSuccessor() {
      if (successor != null && successor.eIsProxy()) {
         InternalEObject oldSuccessor = (InternalEObject)successor;
         successor = (container.Container)eResolveProxy(oldSuccessor);
         if (successor != oldSuccessor) {
            if (eNotificationRequired())
               eNotify(new ENotificationImpl(this, Notification.RESOLVE, ContainerPackage.CONTAINER__SUCCESSOR, oldSuccessor, successor));
         }
      }
      return successor;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public container.Container basicGetSuccessor() {
      return successor;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void setSuccessor(container.Container newSuccessor) {
      container.Container oldSuccessor = successor;
      successor = newSuccessor;
      if (eNotificationRequired())
         eNotify(new ENotificationImpl(this, Notification.SET, ContainerPackage.CONTAINER__SUCCESSOR, oldSuccessor, successor));
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Stack getOn() {
      if (on != null && on.eIsProxy()) {
         InternalEObject oldOn = (InternalEObject)on;
         on = (Stack)eResolveProxy(oldOn);
         if (on != oldOn) {
            if (eNotificationRequired())
               eNotify(new ENotificationImpl(this, Notification.RESOLVE, ContainerPackage.CONTAINER__ON, oldOn, on));
         }
      }
      return on;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public Stack basicGetOn() {
      return on;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void setOn(Stack newOn) {
      Stack oldOn = on;
      on = newOn;
      if (eNotificationRequired())
         eNotify(new ENotificationImpl(this, Notification.SET, ContainerPackage.CONTAINER__ON, oldOn, on));
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Object eGet(int featureID, boolean resolve, boolean coreType) {
      switch (featureID) {
         case ContainerPackage.CONTAINER__ID:
            return getId();
         case ContainerPackage.CONTAINER__ON_TOP_OF:
            if (resolve) return getOnTopOf();
            return basicGetOnTopOf();
         case ContainerPackage.CONTAINER__SUCCESSOR:
            if (resolve) return getSuccessor();
            return basicGetSuccessor();
         case ContainerPackage.CONTAINER__ON:
            if (resolve) return getOn();
            return basicGetOn();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void eSet(int featureID, Object newValue) {
      switch (featureID) {
         case ContainerPackage.CONTAINER__ID:
            setId((String)newValue);
            return;
         case ContainerPackage.CONTAINER__ON_TOP_OF:
            setOnTopOf((container.Container)newValue);
            return;
         case ContainerPackage.CONTAINER__SUCCESSOR:
            setSuccessor((container.Container)newValue);
            return;
         case ContainerPackage.CONTAINER__ON:
            setOn((Stack)newValue);
            return;
      }
      super.eSet(featureID, newValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void eUnset(int featureID) {
      switch (featureID) {
         case ContainerPackage.CONTAINER__ID:
            setId(ID_EDEFAULT);
            return;
         case ContainerPackage.CONTAINER__ON_TOP_OF:
            setOnTopOf((container.Container)null);
            return;
         case ContainerPackage.CONTAINER__SUCCESSOR:
            setSuccessor((container.Container)null);
            return;
         case ContainerPackage.CONTAINER__ON:
            setOn((Stack)null);
            return;
      }
      super.eUnset(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public boolean eIsSet(int featureID) {
      switch (featureID) {
         case ContainerPackage.CONTAINER__ID:
            return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
         case ContainerPackage.CONTAINER__ON_TOP_OF:
            return onTopOf != null;
         case ContainerPackage.CONTAINER__SUCCESSOR:
            return successor != null;
         case ContainerPackage.CONTAINER__ON:
            return on != null;
      }
      return super.eIsSet(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public String toString() {
      if (eIsProxy()) return super.toString();

      StringBuilder result = new StringBuilder(super.toString());
      result.append(" (id: ");
      result.append(id);
      result.append(')');
      return result.toString();
   }

} //ContainerImpl
