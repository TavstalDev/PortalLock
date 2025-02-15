package io.github.tavstal.portallock.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark a field as a configuration field with an optional comment.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigField {
    /**
     * Optional comment for the configuration field.
     *
     * @return The comment as a string.
     */
    String comment() default "";
}
