package org.nigelsim.adapters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that we adapt for this class.
 * The interfaces we adapt for should be obvious (ie, we implement them).
 * @author nigel
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Adapter {
	Class forClass();
}
