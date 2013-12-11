package com.enonic.wem.portal.resource;

import java.nio.file.Files;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.common.net.MediaType;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.module.ModuleKeyResolver;
import com.enonic.wem.core.module.ModuleKeyResolverService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.util.MediaTypes;

import static com.enonic.wem.api.command.Commands.content;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

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

    @Inject
    protected ModuleKeyResolverService moduleKeyResolver;

    @Inject
    protected Client client;

    @GET
    public Response getResource()
    {
        final Content content = resolveContent();
        if ( content == null )
        {
            return Response.status( NOT_FOUND ).build();
        }

        final ModuleKey moduleKey = resolveModule( content.getPath() );
        if ( moduleKey == null )
        {
            return Response.status( NOT_FOUND ).build();
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
            return Response.status( NOT_FOUND ).build();
        }
    }

    private Content resolveContent()
    {
        final ContentPath contentPath = ContentPath.from( this.contentPath );
        return client.execute( content().get().byPath( contentPath ) );
    }

    private ModuleKey resolveModule( final ContentPath path )
    {
        try
        {
            // module key with version
            return ModuleKey.from( moduleName );
        }
        catch ( IllegalArgumentException e )
        {
            // just module name, needs resolving to module key
            final ModuleKeyResolver moduleResolver = moduleKeyResolver.getModuleKeyResolverForContent( path );
            return moduleResolver.resolve( ModuleName.from( moduleName ) );
        }
    }

}
