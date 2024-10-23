package io.github.tavstal.portallock.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigField {
    int order();
    String comment() default "";
}
