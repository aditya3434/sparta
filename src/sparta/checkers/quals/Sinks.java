package sparta.checkers.quals;

import java.lang.annotation.*;

import checkers.quals.*;

/**
 * List of data flow sinks that are attached to a certain piece of data.
 * SPARTA_Permission.ANY is the bottom type.
 * The empty set is the top type.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE, 
    /* The following only added to make Eclipse work. */
    ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.LOCAL_VARIABLE})
@TypeQualifier
@SubtypeOf({})
public @interface Sinks {

    

    /**
     * By default we allow no sinks.
     * There is always a @Sinks annotation and this default
     * ensures that the annotation has no effect.
     */
    SPARTA_Permission[] value() default {};
}