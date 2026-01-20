package com.enonic.xp.portal.impl.handler;

import java.util.EnumSet;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.HexCoder;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static com.google.common.base.Strings.nullToEmpty;

@Component(service = AssetHandler.class, configurationPid = "com.enonic.xp.portal")
public class AssetHandler
{
    private static final Pattern PATTERN = Pattern.compile( "^([^/:]+)(?::([^/]+))?/(.+)" );

    private static final EnumSet<HttpMethod> ALLOWED_METHODS = EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS );

    private static final Predicate<WebRequest> IS_GET_HEAD_OPTIONS_METHOD = req -> ALLOWED_METHODS.contains( req.getMethod() );

    private final ResourceService resourceService;

    private volatile String cacheControlHeader;

    @Activate
    public AssetHandler( @Reference final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        cacheControlHeader = config.asset_cacheControl();
    }

    public WebResponse handle( final WebRequest webRequest )
        throws Exception
    {
        final String restPath = HandlerHelper.findRestPath( webRequest, "asset" );

        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw WebException.notFound( "Not a valid asset url pattern" );
        }

        if ( !IS_GET_HEAD_OPTIONS_METHOD.test( webRequest ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", webRequest.getMethod() ) );
        }

        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( ALLOWED_METHODS );
        }

        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
        final String fingerprint = matcher.group( 2 );
        final String path = matcher.group( 3 );

        final ResourceKey assetsKey = ResourceKey.assets( applicationKey );
        final String assetPath = assetsKey.getPath() + path;

        final ResourceKey resourceKey = ResourceKey.from( applicationKey, assetPath );

        final Resource resource = resolveResource( resourceKey );

        final PortalResponse.Builder portalResponse =
            PortalResponse.create().contentType( MediaTypes.instance().fromFile( resource.getKey().getName() ) ).body( resource );

        if ( !nullToEmpty( fingerprint ).isBlank() && !nullToEmpty( cacheControlHeader ).isBlank() && RunMode.isProd() &&
            resourceKey.getPath().equals( assetPath ) && fingerprintMatches( applicationKey, fingerprint ) )
        {
            portalResponse.header( HttpHeaders.CACHE_CONTROL, cacheControlHeader );
        }
        return portalResponse.build();
    }

    private Resource resolveResource( final ResourceKey resourceKey )
    {
        final Resource resource = resourceService.getResource( resourceKey );
        if ( !resource.exists() )
        {
            throw WebException.notFound( String.format( "Resource [%s] not found", resourceKey ) );
        }
        return resource;
    }

    private boolean fingerprintMatches( ApplicationKey applicationKey, String providedFingerprint )
    {
        final Resource resource = resourceService.getResource( ResourceKey.from( applicationKey, "META-INF/MANIFEST.MF" ) );
        return resource.exists() && HexCoder.toHex( resource.getTimestamp() ).equals( providedFingerprint );
    }
}
