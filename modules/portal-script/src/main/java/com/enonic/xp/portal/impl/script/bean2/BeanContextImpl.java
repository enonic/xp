package com.enonic.xp.portal.impl.script.bean2;

import java.util.function.Supplier;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.bean.BeanContext;
import com.enonic.xp.portal.impl.script.ScriptExecutor;
import com.enonic.xp.portal.impl.script.bean.JsObjectConverter;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.ResourceKey;

public final class BeanContextImpl
    implements BeanContext
{
    private ResourceKey resource;

    private ScriptExecutor executor;

    @Override
    public ModuleKey getModule()
    {
        return this.resource.getModule();
    }

    @Override
    public ResourceKey getResource()
    {
        return this.resource;
    }

    @Override
    public Supplier<PortalRequest> getRequest()
    {
        return PortalRequestAccessor::get;
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
