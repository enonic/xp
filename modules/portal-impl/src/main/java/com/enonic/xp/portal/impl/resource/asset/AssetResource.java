package com.enonic.xp.portal.impl.resource.asset;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;
import com.enonic.xp.util.MediaTypes;

import static com.google.common.primitives.Ints.checkedCast;

public final class AssetResource
    extends BaseSubResource
{
    private Module module;

    private URL resourceUrl;

    @GET
    @Path("{module}/{path:.+}")
    @GZIP
    public Response handle( @PathParam("module") final String module, @PathParam("path") final String path )
        throws Exception
    {
        resolveModule( module );
        resolveResourceUrl( path );
        return doHandle();
    }

    private void resolveModule( final String key )
    {
        final ModuleKey moduleKey = ModuleKey.from( key );
        this.module = this.services.getModuleService().getModule( moduleKey );
        if ( this.module == null )
        {
            throw notFound( "Module [%s] not found", moduleKey );
        }
    }

    private void resolveResourceUrl( final String path )
    {
        this.resourceUrl = this.module.getResource( "app/assets/" + path );
        if ( this.resourceUrl == null )
        {
            throw notFound( "File [%s] not found in module [%s]", path, this.module.getKey().toString() );
        }
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
