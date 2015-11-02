package com.enonic.xp.script.bean;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

public interface BeanContext
{
    ApplicationKey getApplicationKey();

    ResourceKey getResourceKey();

    <T> Supplier<T> getBinding( Class<T> type );

    <T> Supplier<T> getService( Class<T> type );
}
