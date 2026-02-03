package com.enonic.xp.portal.impl.url;

import java.util.HexFormat;
import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.server.RunMode;

final class AssetPathSupplier
    implements Supplier<String>
{
    private final ResourceService resourceService;

    private final String application;

    private final String path;

    AssetPathSupplier( final ResourceService resourceService, final String application, final String path )
    {
        this.resourceService = resourceService;
        this.application = application;
        this.path = path;
    }

    @Override
    public String get()
    {
        final StringBuilder url = new StringBuilder();

        UrlBuilderHelper.appendSubPath( url, "asset" );

        final ApplicationKey applicationKey =
            new ApplicationResolver().portalRequest( PortalRequestAccessor.get() ).application( application ).resolve();

        final Resource resource = this.resourceService.getResource( ResourceKey.from( applicationKey, "META-INF/MANIFEST.MF" ) );
        if ( !resource.exists() )
        {
            throw new IllegalArgumentException( "Could not find application [" + applicationKey + "]" );
        }

        final String fingerprint = RunMode.isDev() ? String.valueOf( stableTime() ) : HexFormat.of().toHexDigits( resource.getTimestamp() );

        UrlBuilderHelper.appendPart( url, applicationKey + ":" + fingerprint );
        UrlBuilderHelper.appendAndEncodePathParts( url, path );

        return url.toString();
    }

    private static long stableTime()
    {
        final Long localScopeTime = (Long) ContextAccessor.current().getLocalScope().getAttribute( "__currentTimeMillis" );
        return Objects.requireNonNullElseGet( localScopeTime, System::currentTimeMillis );
    }
}
