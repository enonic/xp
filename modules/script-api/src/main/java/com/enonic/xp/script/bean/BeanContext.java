package com.enonic.xp.script.bean;

import java.util.function.Supplier;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceKey;

public interface BeanContext
{
    Application getApplication();

    ResourceKey getResourceKey();

    <T> Supplier<T> getAttribute( Class<T> type );

    <T> Supplier<T> getService( Class<T> type );
}
