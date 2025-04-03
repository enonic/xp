package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;

final class ApiUrlApplicationResolver
    implements Supplier<String>
{
    private final String application;

    ApiUrlApplicationResolver( final String application )
    {
        this.application = application;
    }

    @Override
    public String get()
    {
        if ( application != null )
        {
            return application;
        }

        final PortalRequest portalRequest = PortalRequestAccessor.get();

        if ( portalRequest == null || portalRequest.getApplicationKey() == null )
        {
            throw new IllegalArgumentException( "Application must be provided" );
        }

        return portalRequest.getApplicationKey().toString();
    }
}
