package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.google.common.collect.Multimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.util.HashCode;

final class AssetUrlBuilder
    extends GenericEndpointUrlBuilder<AssetUrlParams>
{
    public AssetUrlBuilder()
    {
        super( "asset" );
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );

        final ApplicationKey applicationKey = new ApplicationResolver().
            portalRequest( this.portalRequest ).
            application( this.params.getApplication() ).
            resolve();

        final String assetsHash = this.resourceService.resourceHash( ResourceKey.assets( applicationKey ) ).map( HashCode::toString ).
            orElseThrow( () -> new IllegalArgumentException( "Could not find application [" + applicationKey + "]" ) );

        final String fingerprint = RunMode.get() == RunMode.DEV ? Long.toString( stableTime() ) : assetsHash;

        appendPart( url, applicationKey + ":" + fingerprint );
        appendPart( url, this.params.getPath() );
    }

    private static long stableTime()
    {
        final Long localScopeTime = (Long) ContextAccessor.current().getLocalScope().getAttribute( "__currentTimeMillis" );
        return Objects.requireNonNullElse( localScopeTime, System.currentTimeMillis() );
    }
}
