/**
 */
package PacmanGame.impl;

import PacmanGame.MoveableEntity;
import PacmanGame.PacmanGamePackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Moveable Entity</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public abstract class MoveableEntityImpl extends PositionableEntityImpl implements MoveableEntity {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MoveableEntityImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PacmanGamePackage.Literals.MOVEABLE_ENTITY;
	}

} //MoveableEntityImpl
