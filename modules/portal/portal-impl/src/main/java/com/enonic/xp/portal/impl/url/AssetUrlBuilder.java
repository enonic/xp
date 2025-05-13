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
import com.enonic.xp.web.servlet.UriRewritingResult;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

final class AssetUrlBuilder
    extends PortalUrlBuilder<AssetUrlParams>
{
    private boolean useLegacyContextPath;

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
            UrlBuilderHelper.appendAndEncodePathParts( url, this.portalRequest.getContentPath().toString() );
        }
        else
        {
            url.setLength( 0 );

            final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( this.portalRequest.getRawRequest() );
            UrlBuilderHelper.appendSubPath( url, virtualHost.getTarget() );
        }

        UrlBuilderHelper.appendSubPath( url, "_" );
        UrlBuilderHelper.appendSubPath( url, "asset" );

        final ApplicationKey applicationKey =
            new ApplicationResolver().portalRequest( this.portalRequest ).application( this.params.getApplication() ).resolve();

        final Resource resource = this.resourceService.getResource( ResourceKey.from( applicationKey, "META-INF/MANIFEST.MF" ) );
        if ( !resource.exists() )
        {
            throw new IllegalArgumentException( "Could not find application [" + applicationKey + "]" );
        }

        final String fingerprint =
            RunMode.get() == RunMode.DEV ? String.valueOf( stableTime() ) : HexCoder.toHex( resource.getTimestamp() );

        UrlBuilderHelper.appendPart( url, applicationKey + ":" + fingerprint );
        UrlBuilderHelper.appendAndEncodePathParts( url, this.params.getPath() );
    }

    @Override
    protected String postUriRewriting( final UriRewritingResult uriRewritingResult )
    {
        return new LegacyVhostUrlPostRewriter( uriRewritingResult, this.portalRequest, "asset" ).rewrite();
    }

    private static long stableTime()
    {
        final Long localScopeTime = (Long) ContextAccessor.current().getLocalScope().getAttribute( "__currentTimeMillis" );
        return Objects.requireNonNullElseGet( localScopeTime, System::currentTimeMillis );
    }
}
