package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;

class AppKeyResolver
{
    static @NonNull ApplicationKey resolve( @Nullable String appKey )
    {
        return Optional.ofNullable( appKey ).map( ApplicationKey::from ).orElseGet( () -> {
            final PortalRequest portalRequest = Objects.requireNonNull( PortalRequestAccessor.get(), "no request bound" );

            return Objects.requireNonNull( portalRequest.getApplicationKey(), "no application in request" );
        } );
    }
}
