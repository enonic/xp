package com.enonic.wem.portal.underscore;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.common.net.MediaType;
import com.sun.jersey.api.core.InjectParam;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
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
    {
        final ModuleKey moduleKey = resolveModule( params.content, params.module );
        final ModuleResourceKey moduleResource = ModuleResourceKey.from( moduleKey, "public/" + params.resource );
        final File fileResource = this.modulePathResolver.resolveResourcePath( moduleResource ).toFile();

        if ( !fileResource.isFile() )
        {
            throw notFound();
        }

        final MediaType mediaType = MediaTypes.instance().fromFile( fileResource.getName() );
        return Response.ok( fileResource ).type( mediaType.toString() ).build();
    }
}
