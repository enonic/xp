package com.enonic.xp.portal.bean;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

public interface BeanContext
{
    ApplicationKey getApplication();

    ResourceKey getResource();

    <T> Supplier<T> getService( Class<T> type );
}
