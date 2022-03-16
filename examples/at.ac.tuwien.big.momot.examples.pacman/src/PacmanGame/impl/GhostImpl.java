/**
 */
package PacmanGame.impl;

import PacmanGame.Ghost;
import PacmanGame.PacmanGamePackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ghost</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class GhostImpl extends MoveableEntityImpl implements Ghost {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GhostImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PacmanGamePackage.Literals.GHOST;
	}

} //GhostImpl
