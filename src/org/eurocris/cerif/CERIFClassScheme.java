/**
 * 
 */
package org.eurocris.cerif;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

@Documented
@Target( TYPE )
/**
 * A CERIF Classification Scheme.
 * @author jdvorak
 */
public @interface CERIFClassScheme {

	/**
	 * The English name of the class scheme. (cfClassScheme/cfName)
	 */
	String name();

	/**
	 * The identifier of the class scheme. (cfClassScheme/cfClassSchemeId)
	 */
	String id();

}
