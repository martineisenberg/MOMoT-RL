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
 * An implementation of the model object '<em><b>Stack</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link container.impl.StackImpl#getId <em>Id</em>}</li>
 *   <li>{@link container.impl.StackImpl#getTopContainer <em>Top Container</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StackImpl extends MinimalEObjectImpl.Container implements Stack {
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
    * The cached value of the '{@link #getTopContainer() <em>Top Container</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getTopContainer()
    * @generated
    * @ordered
    */
   protected container.Container topContainer;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected StackImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return ContainerPackage.Literals.STACK;
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
         eNotify(new ENotificationImpl(this, Notification.SET, ContainerPackage.STACK__ID, oldId, id));
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public container.Container getTopContainer() {
      if (topContainer != null && topContainer.eIsProxy()) {
         InternalEObject oldTopContainer = (InternalEObject)topContainer;
         topContainer = (container.Container)eResolveProxy(oldTopContainer);
         if (topContainer != oldTopContainer) {
            if (eNotificationRequired())
               eNotify(new ENotificationImpl(this, Notification.RESOLVE, ContainerPackage.STACK__TOP_CONTAINER, oldTopContainer, topContainer));
         }
      }
      return topContainer;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public container.Container basicGetTopContainer() {
      return topContainer;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void setTopContainer(container.Container newTopContainer) {
      container.Container oldTopContainer = topContainer;
      topContainer = newTopContainer;
      if (eNotificationRequired())
         eNotify(new ENotificationImpl(this, Notification.SET, ContainerPackage.STACK__TOP_CONTAINER, oldTopContainer, topContainer));
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Object eGet(int featureID, boolean resolve, boolean coreType) {
      switch (featureID) {
         case ContainerPackage.STACK__ID:
            return getId();
         case ContainerPackage.STACK__TOP_CONTAINER:
            if (resolve) return getTopContainer();
            return basicGetTopContainer();
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
         case ContainerPackage.STACK__ID:
            setId((String)newValue);
            return;
         case ContainerPackage.STACK__TOP_CONTAINER:
            setTopContainer((container.Container)newValue);
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
         case ContainerPackage.STACK__ID:
            setId(ID_EDEFAULT);
            return;
         case ContainerPackage.STACK__TOP_CONTAINER:
            setTopContainer((container.Container)null);
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
         case ContainerPackage.STACK__ID:
            return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
         case ContainerPackage.STACK__TOP_CONTAINER:
            return topContainer != null;
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

} //StackImpl
