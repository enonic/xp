package com.enonic.xp.portal.impl.url;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;

final class ApplicationResolver
{
    private PortalRequest portalRequest;

    private String application;

    public ApplicationResolver portalRequest( final PortalRequest portalRequest )
    {
        this.portalRequest = portalRequest;
        return this;
    }

    public ApplicationResolver application( final String application )
    {
        this.application = application;
        return this;
    }

    public ApplicationKey resolve()
    {
        if ( this.application != null )
        {
            return ApplicationKey.from( this.application );
        }

        return this.portalRequest.getApplicationKey();
    }
}
