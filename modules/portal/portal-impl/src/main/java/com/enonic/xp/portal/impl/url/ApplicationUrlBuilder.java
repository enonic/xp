package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.url.ApplicationUrlParams;

final class ApplicationUrlBuilder
    extends PortalUrlBuilder<ApplicationUrlParams>
{
    private ApplicationKey getApplication()
    {
        return new ApplicationResolver().
            portalRequest( this.portalRequest ).
            application( this.params.getApplication() ).
            resolve();
    }

    @Override
    protected String getBaseUri()
    {
        return "/app";
    }

    @Override
    protected boolean isPortalBase()
    {
        return false;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, getApplication().toString() );
        appendPart( url, this.params.getPath() );
    }
}
