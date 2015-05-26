package org.cloudifysource.restDoclet.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a controller parameter as being a command object whose @JsonProperty
 * annotated constructor or whose setters need documenting.
 *
 * @author bruce
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentCommand {
}
