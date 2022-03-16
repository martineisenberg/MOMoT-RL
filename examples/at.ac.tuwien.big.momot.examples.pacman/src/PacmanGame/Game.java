/**
 */
package PacmanGame;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Game</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link PacmanGame.Game#getGridnodes <em>Gridnodes</em>}</li>
 *   <li>{@link PacmanGame.Game#getScoreboard <em>Scoreboard</em>}</li>
 *   <li>{@link PacmanGame.Game#getEntites <em>Entites</em>}</li>
 * </ul>
 *
 * @see PacmanGame.PacmanGamePackage#getGame()
 * @model
 * @generated
 */
public interface Game extends EObject {
	/**
	 * Returns the value of the '<em><b>Gridnodes</b></em>' containment reference list.
	 * The list contents are of type {@link PacmanGame.GridNode}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gridnodes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gridnodes</em>' containment reference list.
	 * @see PacmanGame.PacmanGamePackage#getGame_Gridnodes()
	 * @model containment="true"
	 * @generated
	 */
	EList<GridNode> getGridnodes();

	/**
	 * Returns the value of the '<em><b>Scoreboard</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Scoreboard</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Scoreboard</em>' containment reference.
	 * @see #setScoreboard(Scoreboard)
	 * @see PacmanGame.PacmanGamePackage#getGame_Scoreboard()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Scoreboard getScoreboard();

	/**
	 * Sets the value of the '{@link PacmanGame.Game#getScoreboard <em>Scoreboard</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Scoreboard</em>' containment reference.
	 * @see #getScoreboard()
	 * @generated
	 */
	void setScoreboard(Scoreboard value);

	/**
	 * Returns the value of the '<em><b>Entites</b></em>' containment reference list.
	 * The list contents are of type {@link PacmanGame.PositionableEntity}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Entites</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entites</em>' containment reference list.
	 * @see PacmanGame.PacmanGamePackage#getGame_Entites()
	 * @model containment="true"
	 * @generated
	 */
	EList<PositionableEntity> getEntites();

} // Game
