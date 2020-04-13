package com.aegisql.java_path;

import java.lang.annotation.*;

/**
 * The Interface NoLabel.
 * Exclude method from label matching, if multiple choices available
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoPathElement {
}
