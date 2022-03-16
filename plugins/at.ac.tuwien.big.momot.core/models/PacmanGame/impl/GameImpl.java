/**
 */
package PacmanGame.impl;

import PacmanGame.Game;
import PacmanGame.GridNode;
import PacmanGame.PacmanGamePackage;
import PacmanGame.PositionableEntity;
import PacmanGame.Scoreboard;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Game</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link PacmanGame.impl.GameImpl#getGridnodes <em>Gridnodes</em>}</li>
 *   <li>{@link PacmanGame.impl.GameImpl#getScoreboard <em>Scoreboard</em>}</li>
 *   <li>{@link PacmanGame.impl.GameImpl#getEntites <em>Entites</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GameImpl extends MinimalEObjectImpl.Container implements Game {
	/**
	 * The cached value of the '{@link #getGridnodes() <em>Gridnodes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGridnodes()
	 * @generated
	 * @ordered
	 */
	protected EList<GridNode> gridnodes;

	/**
	 * The cached value of the '{@link #getScoreboard() <em>Scoreboard</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScoreboard()
	 * @generated
	 * @ordered
	 */
	protected Scoreboard scoreboard;

	/**
	 * The cached value of the '{@link #getEntites() <em>Entites</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntites()
	 * @generated
	 * @ordered
	 */
	protected EList<PositionableEntity> entites;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GameImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PacmanGamePackage.Literals.GAME;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<GridNode> getGridnodes() {
		if (gridnodes == null) {
			gridnodes = new EObjectContainmentEList<GridNode>(GridNode.class, this, PacmanGamePackage.GAME__GRIDNODES);
		}
		return gridnodes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetScoreboard(Scoreboard newScoreboard, NotificationChain msgs) {
		Scoreboard oldScoreboard = scoreboard;
		scoreboard = newScoreboard;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PacmanGamePackage.GAME__SCOREBOARD, oldScoreboard, newScoreboard);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setScoreboard(Scoreboard newScoreboard) {
		if (newScoreboard != scoreboard) {
			NotificationChain msgs = null;
			if (scoreboard != null)
				msgs = ((InternalEObject)scoreboard).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PacmanGamePackage.GAME__SCOREBOARD, null, msgs);
			if (newScoreboard != null)
				msgs = ((InternalEObject)newScoreboard).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PacmanGamePackage.GAME__SCOREBOARD, null, msgs);
			msgs = basicSetScoreboard(newScoreboard, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PacmanGamePackage.GAME__SCOREBOARD, newScoreboard, newScoreboard));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<PositionableEntity> getEntites() {
		if (entites == null) {
			entites = new EObjectContainmentEList<PositionableEntity>(PositionableEntity.class, this, PacmanGamePackage.GAME__ENTITES);
		}
		return entites;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PacmanGamePackage.GAME__GRIDNODES:
				return ((InternalEList<?>)getGridnodes()).basicRemove(otherEnd, msgs);
			case PacmanGamePackage.GAME__SCOREBOARD:
				return basicSetScoreboard(null, msgs);
			case PacmanGamePackage.GAME__ENTITES:
				return ((InternalEList<?>)getEntites()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PacmanGamePackage.GAME__GRIDNODES:
				return getGridnodes();
			case PacmanGamePackage.GAME__SCOREBOARD:
				return getScoreboard();
			case PacmanGamePackage.GAME__ENTITES:
				return getEntites();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
		@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PacmanGamePackage.GAME__GRIDNODES:
				getGridnodes().clear();
				getGridnodes().addAll((Collection<? extends GridNode>)newValue);
				return;
			case PacmanGamePackage.GAME__SCOREBOARD:
				setScoreboard((Scoreboard)newValue);
				return;
			case PacmanGamePackage.GAME__ENTITES:
				getEntites().clear();
				getEntites().addAll((Collection<? extends PositionableEntity>)newValue);
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
			case PacmanGamePackage.GAME__GRIDNODES:
				getGridnodes().clear();
				return;
			case PacmanGamePackage.GAME__SCOREBOARD:
				setScoreboard((Scoreboard)null);
				return;
			case PacmanGamePackage.GAME__ENTITES:
				getEntites().clear();
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
			case PacmanGamePackage.GAME__GRIDNODES:
				return gridnodes != null && !gridnodes.isEmpty();
			case PacmanGamePackage.GAME__SCOREBOARD:
				return scoreboard != null;
			case PacmanGamePackage.GAME__ENTITES:
				return entites != null && !entites.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //GameImpl
