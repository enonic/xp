package com.enonic.xp.script.impl.bean;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.impl.executor.ScriptExecutor;

public final class BeanContextImpl
    implements BeanContext
{
    private ResourceKey resourceKey;

    private ScriptExecutor executor;

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
        return this.executor.getServiceRegistry().getService( type );
    }

    @Override
    public <T> Supplier<T> getBinding( final Class<T> type )
    {
        final Supplier<T> supplier = this.executor.getScriptSettings().getBinding( type );
        return supplier != null ? supplier : () -> null;
    }

    public void setResourceKey( final ResourceKey resourceKey )
    {
        this.resourceKey = resourceKey;
    }

    public void setExecutor( final ScriptExecutor executor )
    {
        this.executor = executor;
    }
}
