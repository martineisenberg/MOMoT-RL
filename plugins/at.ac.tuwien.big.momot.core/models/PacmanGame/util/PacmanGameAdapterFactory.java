/**
 */
package PacmanGame.util;

import PacmanGame.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see PacmanGame.PacmanGamePackage
 * @generated
 */
public class PacmanGameAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static PacmanGamePackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PacmanGameAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = PacmanGamePackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PacmanGameSwitch<Adapter> modelSwitch =
		new PacmanGameSwitch<Adapter>() {
			@Override
			public Adapter caseGridNode(GridNode object) {
				return createGridNodeAdapter();
			}
			@Override
			public Adapter caseFood(Food object) {
				return createFoodAdapter();
			}
			@Override
			public Adapter casePacman(Pacman object) {
				return createPacmanAdapter();
			}
			@Override
			public Adapter caseGhost(Ghost object) {
				return createGhostAdapter();
			}
			@Override
			public Adapter caseScoreboard(Scoreboard object) {
				return createScoreboardAdapter();
			}
			@Override
			public Adapter caseGame(Game object) {
				return createGameAdapter();
			}
			@Override
			public Adapter casePositionableEntity(PositionableEntity object) {
				return createPositionableEntityAdapter();
			}
			@Override
			public Adapter caseMoveableEntity(MoveableEntity object) {
				return createMoveableEntityAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link PacmanGame.GridNode <em>Grid Node</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see PacmanGame.GridNode
	 * @generated
	 */
	public Adapter createGridNodeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link PacmanGame.Food <em>Food</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see PacmanGame.Food
	 * @generated
	 */
	public Adapter createFoodAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link PacmanGame.Pacman <em>Pacman</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see PacmanGame.Pacman
	 * @generated
	 */
	public Adapter createPacmanAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link PacmanGame.Ghost <em>Ghost</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see PacmanGame.Ghost
	 * @generated
	 */
	public Adapter createGhostAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link PacmanGame.Scoreboard <em>Scoreboard</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see PacmanGame.Scoreboard
	 * @generated
	 */
	public Adapter createScoreboardAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link PacmanGame.Game <em>Game</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see PacmanGame.Game
	 * @generated
	 */
	public Adapter createGameAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link PacmanGame.PositionableEntity <em>Positionable Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see PacmanGame.PositionableEntity
	 * @generated
	 */
	public Adapter createPositionableEntityAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link PacmanGame.MoveableEntity <em>Moveable Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see PacmanGame.MoveableEntity
	 * @generated
	 */
	public Adapter createMoveableEntityAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //PacmanGameAdapterFactory
