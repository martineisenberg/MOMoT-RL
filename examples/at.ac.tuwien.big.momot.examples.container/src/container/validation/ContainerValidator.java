/**
 *
 * $Id$
 */
package container.validation;

import container.Container;
import container.Stack;

/**
 * A sample validator interface for {@link container.Container}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface ContainerValidator {
   boolean validate();

   boolean validateId(String value);
   boolean validateOnTopOf(Container value);
   boolean validateSuccessor(Container value);
   boolean validateOn(Stack value);
}
