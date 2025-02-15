package io.github.tavstal.portallock.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark a field as a value editor with a specified field type.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueEditor {
    /**
     * Specifies the type of the field.
     *
     * @return The field type as an EFieldType enum.
     */
    EFieldType type();
}