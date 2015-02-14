package com.enonic.xp.portal.impl.url;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;

final class ModuleResolver
{
    private PortalContext context;

    private String module;

    public ModuleResolver context( final PortalContext context )
    {
        this.context = context;
        return this;
    }

    public ModuleResolver module( final String module )
    {
        this.module = module;
        return this;
    }

    public ModuleKey resolve()
    {
        if ( this.module != null )
        {
            return ModuleKey.from( this.module );
        }

        return this.context.getModule();
    }
}
