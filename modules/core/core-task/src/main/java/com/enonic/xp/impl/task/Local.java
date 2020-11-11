package com.enonic.xp.impl.task;

import org.osgi.service.component.annotations.ComponentPropertyType;

@ComponentPropertyType
public @interface Local
{
    boolean value() default true;
}
