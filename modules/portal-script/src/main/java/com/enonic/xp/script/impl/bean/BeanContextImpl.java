package com.enonic.xp.script.impl.bean;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.bean.BeanContext;

public final class BeanContextImpl
    implements BeanContext
{
    private ResourceKey resource;

    private ScriptExecutor executor;

    @Override
    public ApplicationKey getApplication()
    {
        return this.resource.getApplicationKey();
    }

    @Override
    public ResourceKey getResource()
    {
        return this.resource;
    }

    @Override
    public <T> Supplier<T> getService( final Class<T> type )
    {
        return this.executor.getServiceRegistry().getService( type );
    }

    public void setResource( final ResourceKey resource )
    {
        this.resource = resource;
    }

    public void setExecutor( final ScriptExecutor executor )
    {
        this.executor = executor;
    }
}
