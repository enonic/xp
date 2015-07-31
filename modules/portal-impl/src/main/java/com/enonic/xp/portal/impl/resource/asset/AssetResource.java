package com.enonic.xp.portal.impl.resource.asset;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.util.MediaTypes;

import static com.google.common.primitives.Ints.checkedCast;

public final class AssetResource
    extends BaseSubResource
{
    private URL resourceUrl;

    @GET
    @Path("{module}/{path:.+}")
    @GZIP
    public Response handle( @PathParam("module") final String application, @PathParam("path") final String path )
        throws Exception
    {
        resolveResourceUrl( application, path );
        return doHandle();
    }

    private void resolveResourceUrl( final String key, final String path )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( key );
        final Resource resource = this.services.getResourceService().getResource( ResourceKey.from( applicationKey, "site/assets/" + path ) );

        if ( resource == null )
        {
            throw notFound( "Application [%s] or file [%s] in it not found", key, path );
        }

        this.resourceUrl = resource.getUrl();
    }

    private void setCacheHeaders( final Response.ResponseBuilder response )
    {
        if ( this.mode == RenderMode.LIVE )
        {
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setMaxAge( checkedCast( TimeUnit.MINUTES.toSeconds( 10 ) ) );
            response.cacheControl( cacheControl );
        }
    }

    private Response doHandle()
        throws Exception
    {
        final String type = MediaTypes.instance().fromFile( this.resourceUrl.toExternalForm() ).toString();
        final Response.ResponseBuilder response = Response.ok().type( type );
        setCacheHeaders( response );
        return response.entity( this.resourceUrl.openStream() ).build();
    }
}
