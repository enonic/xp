package com.enonic.xp.core.internal;

import org.osgi.service.component.annotations.ComponentPropertyType;

/**
 * A helper annotation to mark OSGi Component services as "local".
 * Useful when same interface is used to provide local and cluster global service.
 * To get such one referenced one should use {@code (local=true)} filter.
 */
@ComponentPropertyType
public @interface Local
{
    boolean value() default true;
}
