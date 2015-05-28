package com.enonic.xp.portal.impl.url;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;

final class ModuleResolver
{
    private PortalRequest portalRequest;

    private String module;

    public ModuleResolver portalRequest( final PortalRequest portalRequest )
    {
        this.portalRequest = portalRequest;
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

        return this.portalRequest.getModule();
    }
}
