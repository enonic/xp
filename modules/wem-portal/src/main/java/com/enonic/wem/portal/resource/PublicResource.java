package com.enonic.wem.portal.resource;

import java.nio.file.Files;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.common.net.MediaType;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.util.MediaTypes;

@Path("{mode}/{path:.+}/_/public/{module}/{resource:.+}")
public final class PublicResource
{
    protected static final ResourcePath PUBLIC_PATH = ResourcePath.from( "public" );

    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentPath;

    @PathParam("module")
    protected String moduleName;

    @PathParam("resource")
    protected String resourceName;

    @Inject
    protected ModuleResourcePathResolver modulePathResolver;

    @GET
    public Response getResource()
    {
        final ModuleKey moduleKey = resolveModule();
        if ( moduleKey == null )
        {
            return Response.status( Response.Status.NOT_FOUND ).build();
        }

        final ResourcePath resourcePath = PUBLIC_PATH.resolve( resourceName );
        final ModuleResourceKey moduleResource = new ModuleResourceKey( moduleKey, resourcePath );
        final java.nio.file.Path resourceFileSystemPath = modulePathResolver.resolveResourcePath( moduleResource );

        if ( Files.isRegularFile( resourceFileSystemPath ) )
        {
            final String fileName = resourceFileSystemPath.getFileName().toString();
            final MediaType mediaType = MediaTypes.instance().fromFile( fileName );
            return Response.ok( resourceFileSystemPath.toFile() ).type( mediaType.toString() ).build();
        }
        else
        {
            return Response.status( Response.Status.NOT_FOUND ).build();
        }
    }

    private ModuleKey resolveModule()
    {
        try
        {
            return ModuleKey.from( moduleName );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
    }

}
