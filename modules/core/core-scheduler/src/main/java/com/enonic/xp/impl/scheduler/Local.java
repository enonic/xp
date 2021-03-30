package com.enonic.xp.impl.scheduler;

import org.osgi.service.component.annotations.ComponentPropertyType;

@ComponentPropertyType
@interface Local
{
    boolean value() default true;
}
