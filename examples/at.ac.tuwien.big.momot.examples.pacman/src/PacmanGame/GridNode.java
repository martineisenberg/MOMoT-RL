/**
 */
package PacmanGame;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Grid Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link PacmanGame.GridNode#getTop <em>Top</em>}</li>
 *   <li>{@link PacmanGame.GridNode#getBottom <em>Bottom</em>}</li>
 *   <li>{@link PacmanGame.GridNode#getLeft <em>Left</em>}</li>
 *   <li>{@link PacmanGame.GridNode#getRight <em>Right</em>}</li>
 *   <li>{@link PacmanGame.GridNode#getId <em>Id</em>}</li>
 * </ul>
 *
 * @see PacmanGame.PacmanGamePackage#getGridNode()
 * @model
 * @generated
 */
public interface GridNode extends EObject {
	/**
	 * Returns the value of the '<em><b>Top</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Top</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Top</em>' reference.
	 * @see #setTop(GridNode)
	 * @see PacmanGame.PacmanGamePackage#getGridNode_Top()
	 * @model
	 * @generated
	 */
	GridNode getTop();

	/**
	 * Sets the value of the '{@link PacmanGame.GridNode#getTop <em>Top</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Top</em>' reference.
	 * @see #getTop()
	 * @generated
	 */
	void setTop(GridNode value);

	/**
	 * Returns the value of the '<em><b>Bottom</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Bottom</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bottom</em>' reference.
	 * @see #setBottom(GridNode)
	 * @see PacmanGame.PacmanGamePackage#getGridNode_Bottom()
	 * @model
	 * @generated
	 */
	GridNode getBottom();

	/**
	 * Sets the value of the '{@link PacmanGame.GridNode#getBottom <em>Bottom</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bottom</em>' reference.
	 * @see #getBottom()
	 * @generated
	 */
	void setBottom(GridNode value);

	/**
	 * Returns the value of the '<em><b>Left</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Left</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Left</em>' reference.
	 * @see #setLeft(GridNode)
	 * @see PacmanGame.PacmanGamePackage#getGridNode_Left()
	 * @model
	 * @generated
	 */
	GridNode getLeft();

	/**
	 * Sets the value of the '{@link PacmanGame.GridNode#getLeft <em>Left</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Left</em>' reference.
	 * @see #getLeft()
	 * @generated
	 */
	void setLeft(GridNode value);

	/**
	 * Returns the value of the '<em><b>Right</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Right</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Right</em>' reference.
	 * @see #setRight(GridNode)
	 * @see PacmanGame.PacmanGamePackage#getGridNode_Right()
	 * @model
	 * @generated
	 */
	GridNode getRight();

	/**
	 * Sets the value of the '{@link PacmanGame.GridNode#getRight <em>Right</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Right</em>' reference.
	 * @see #getRight()
	 * @generated
	 */
	void setRight(GridNode value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see PacmanGame.PacmanGamePackage#getGridNode_Id()
	 * @model
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link PacmanGame.GridNode#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // GridNode
