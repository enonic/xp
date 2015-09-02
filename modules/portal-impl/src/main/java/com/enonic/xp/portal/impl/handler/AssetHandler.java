package com.enonic.xp.portal.impl.handler;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.PortalHandler2;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpMethod;

@Component(immediate = true, service = PortalHandler2.class)
public final class AssetHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/(.+)" );

    private final static String ASSET_PREFIX = "site/assets/";

    private final static long CACHE_TIME = TimeUnit.MINUTES.toSeconds( 10 );

    private ResourceService resourceService;

    public AssetHandler()
    {
        super( "asset" );
        setMethodsAllowed( HttpMethod.GET, HttpMethod.HEAD );
    }

    @Override
    protected PortalResponse doHandle( final PortalRequest req )
        throws Exception
    {
        final String restPath = findRestPath( req );
        final ResourceKey resourceKey = resolveResourceKey( restPath );
        final Resource resource = resolveResource( resourceKey );

        final PortalResponse.Builder response = PortalResponse.create();
        response.status( 200 );
        response.body( resource );

        final String type = MediaTypes.instance().fromFile( resource.getKey().getName() ).toString();
        response.contentType( type );

        if ( req.getMode() == RenderMode.LIVE )
        {
            response.header( "Cache-Control", "no-transform, max-age=" + CACHE_TIME );
        }

        return response.build();
    }

    private ResourceKey resolveResourceKey( final String restPath )
    {
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw notFound( "Not a valid asset url pattern" );
        }

        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
        return ResourceKey.from( applicationKey, ASSET_PREFIX + matcher.group( 2 ) );
    }

    private Resource resolveResource( final ResourceKey key )
    {
        final Resource resource = this.resourceService.getResource( key );
        if ( !resource.exists() )
        {
            throw notFound( "Resource [%s] not found", key );
        }

        return resource;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
