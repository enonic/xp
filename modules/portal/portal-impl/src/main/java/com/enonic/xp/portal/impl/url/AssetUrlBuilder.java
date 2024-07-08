package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.google.common.collect.Multimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.internal.HexCoder;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

final class AssetUrlBuilder
    extends GenericEndpointUrlBuilder<AssetUrlParams>
{
    private boolean useLegacyContextPath;

    AssetUrlBuilder()
    {
        super( "asset" );
    }

    public void setUseLegacyContextPath( final boolean useLegacyContextPath )
    {
        this.useLegacyContextPath = useLegacyContextPath;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        if ( useLegacyContextPath )
        {
            super.buildUrl( url, params );
        }
        else
        {
            url.setLength( 0 );

            final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( this.portalRequest.getRawRequest() );
            appendPart( url, virtualHost.getTarget() );
            appendPart( url, "_" );
            appendPart( url, this.endpointType );
        }

        final ApplicationKey applicationKey =
            new ApplicationResolver().portalRequest( this.portalRequest ).application( this.params.getApplication() ).resolve();

        final Resource resource = this.resourceService.getResource( ResourceKey.from( applicationKey, "META-INF/MANIFEST.MF" ) );
        if ( !resource.exists() )
        {
            throw new IllegalArgumentException( "Could not find application [" + applicationKey + "]" );
        }

        final String fingerprint =
            RunMode.get() == RunMode.DEV ? String.valueOf( stableTime() ) : HexCoder.toHex( resource.getTimestamp() );

        appendPart( url, applicationKey + ":" + fingerprint );
        appendPart( url, this.params.getPath() );
    }

    private static long stableTime()
    {
        final Long localScopeTime = (Long) ContextAccessor.current().getLocalScope().getAttribute( "__currentTimeMillis" );
        return Objects.requireNonNullElseGet( localScopeTime, System::currentTimeMillis );
    }
}
