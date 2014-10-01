package com.enonic.wem.portal.internal.base;

import com.enonic.wem.api.module.ModuleService;

public abstract class ModuleBaseResourceFactory<T extends ModuleBaseResource>
    extends BaseResourceFactory<T>
{
    private ModuleService moduleService;

    public ModuleBaseResourceFactory( final Class<T> type )
    {
        super( type );
    }

    protected void configure( final T instance )
    {
        instance.moduleService = this.moduleService;
    }

    public final void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
