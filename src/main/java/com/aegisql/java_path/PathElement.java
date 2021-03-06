package com.aegisql.java_path;

import java.lang.annotation.*;

/**
 * The Interface Label.
 * Allows method or field match on multiple label names
 */
@Documented
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathElement {

    /**
     * Value. - collection of matching labels
     *
     * @return the string[]
     */
    String[] value();
}
