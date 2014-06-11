package com.enonic.wem.portal.underscore;

import java.nio.file.Files;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.common.net.MediaType;
import com.sun.jersey.api.core.InjectParam;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.util.MediaTypes;
import com.enonic.wem.portal.exception.PortalWebException;

@Path("{mode}/{path:.+}/_/public/{module}/{resource:.+}")
public final class OldPublicResource
    extends OldUnderscoreResource
{
    public final class Request
    {
        @PathParam("mode")
        public String mode;

        @PathParam("path")
        public String contentPath;

        @PathParam("module")
        public String moduleName;

        @PathParam("resource")
        public String resourceName;
    }

    @GET
    public Response getResource( @InjectParam final Request request )
    {
        final ModuleKey moduleKey = resolveModule( request.contentPath, request.moduleName );
        final ModuleResourceKey moduleResource = ModuleResourceKey.from( moduleKey, "public/" + request.resourceName );
        final java.nio.file.Path resourceFileSystemPath = modulePathResolver.resolveResourcePath( moduleResource );

        if ( Files.isRegularFile( resourceFileSystemPath ) )
        {
            final String fileName = resourceFileSystemPath.getFileName().toString();
            final MediaType mediaType = MediaTypes.instance().fromFile( fileName );
            return Response.ok( resourceFileSystemPath.toFile() ).type( mediaType.toString() ).build();
        }
        else
        {
            throw PortalWebException.notFound().build();
        }
    }
}
