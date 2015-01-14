package com.enonic.xp.portal.impl.resource.asset;

import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.util.MediaTypes;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;

public final class AssetResource
    extends BaseSubResource
{
    private Module module;

    private URL resourceUrl;

    @GET
    @Path("{module}/{path:.+}")
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
        this.resourceUrl = this.module.getResource( "public/" + path );
        if ( this.resourceUrl == null )
        {
            throw notFound( "File [%s] not found in module [%s]", path, this.module.getKey().toString() );
        }
    }

    private Response doHandle()
        throws Exception
    {
        final String type = MediaTypes.instance().fromFile( this.resourceUrl.toExternalForm() ).toString();
        return Response.ok().type( type ).entity( this.resourceUrl.openStream() ).build();
    }
}
