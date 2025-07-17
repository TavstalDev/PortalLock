package io.github.tavstaldev.portallock.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark a field as a configuration field with an optional comment.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigField {
    /**
     * Specifies the order in which the value editor should be displayed or processed.
     *
     * @return The order as an integer.
     */
    int order();

    /**
     * Optional comment for the configuration field.
     *
     * @return The comment as a string.
     */
    String comment() default "";
}
