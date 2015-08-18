package com.enonic.xp.script.bean;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

public interface BeanContext
{
    ApplicationKey getApplication();

    ResourceKey getResource();

    <T> Supplier<T> getService( Class<T> type );
}
