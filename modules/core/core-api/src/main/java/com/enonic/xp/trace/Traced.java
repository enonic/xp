package com.enonic.xp.trace;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method for load-time trace instrumentation.
 * <p>
 * Classes containing methods annotated with this annotation are rewritten when they are loaded into the OSGi
 * framework: each annotated method is wrapped in a trace scope, equivalent to executing the method body through
 * {@link Tracer}. While the method executes, the trace is available through {@link Tracer#current()} and can be
 * enriched with attributes using {@link Tracer#withCurrent(java.util.function.Consumer)}.
 * <p>
 * When tracing is disabled, or when the class is loaded outside the OSGi framework (for example in unit tests),
 * the annotated method behaves exactly as written.
 * <p>
 * The annotation has no effect on abstract or native methods, or on constructors.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Traced
{
    /**
     * Name of the trace. Use a constant, low-cardinality name (for example {@code "portal.render"}) and record
     * dynamic values as trace attributes instead of encoding them in the name.
     * <p>
     * When empty, the name defaults to {@code SimpleClassName.methodName}.
     *
     * @return trace name
     */
    String value() default "";
}
