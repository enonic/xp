package com.enonic.wem.portal.underscore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.common.net.MediaType;
import com.sun.jersey.api.core.InjectParam;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.util.MediaTypes;
import com.enonic.wem.portal.base.ModuleBaseHandler;

@Path("{mode}/{content:.+}/_/public/{module}/{resource:.+}")
public final class PublicHandler
    extends ModuleBaseHandler
{
    public final static class Params
    {
        @PathParam("mode")
        public String mode;

        @PathParam("content")
        public String content;

        @PathParam("module")
        public String module;

        @PathParam("resource")
        public String resource;
    }

    @GET
    public Response handle( @InjectParam final Params params )
        throws IOException
    {
        final ModuleKey moduleKey = resolveModule( params.content, params.module );
        final Module module;
        try
        {
            module = this.moduleService.getModule( moduleKey );
        }
        catch ( ModuleNotFoundException e )
        {
            throw notFound();
        }

        final URL resource = module.getResource( "public/" + params.resource );
        if ( resource == null )
        {
            throw notFound();
        }

        final MediaType mediaType = MediaTypes.instance().fromFile( resource.getFile() );
        final InputStream resourceStream = resource.openStream();
        return Response.ok( resourceStream ).type( mediaType.toString() ).build();
    }
}
