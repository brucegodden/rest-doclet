package org.cloudifysource.restDoclet.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a command class that requires special processing to find the properties it sets.
 *
 * 1/ We look for constructor parameters annotated with @JsonProperty.
 * 2/ We look for public setter methods in the class and its superclasses.
 *
 * @author bruce
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentCommand {
}
