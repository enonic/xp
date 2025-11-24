package com.enonic.xp.script.impl.bean;

import java.util.function.Supplier;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.runtime.ScriptSettings;

public final class BeanContextImpl
    implements BeanContext
{
    private final ResourceKey resourceKey;

    private final ServiceRegistry serviceRegistry;

    private final ScriptSettings settings;

    private final Application application;

    public BeanContextImpl( final ResourceKey resourceKey, final ServiceRegistry serviceRegistry, final ScriptSettings settings,
                            final Application application )
    {
        this.resourceKey = resourceKey;
        this.serviceRegistry = serviceRegistry;
        this.settings = settings;
        this.application = application;
    }

    @Override
    public ApplicationKey getApplicationKey()
    {
        return this.resourceKey.getApplicationKey();
    }

    @Override
    public ResourceKey getResourceKey()
    {
        return this.resourceKey;
    }

    @Override
    public <T> Supplier<T> getService( final Class<T> type )
    {
        return serviceRegistry.getService( type );
    }

    @Override
    public <T> Supplier<T> getBinding( final Class<T> type )
    {
        if ( type == Application.class )
        {
            return () -> (T) application;
        }

        final Supplier<T> supplier = settings.getBinding( type );
        return supplier != null ? supplier : () -> null;
    }
}
