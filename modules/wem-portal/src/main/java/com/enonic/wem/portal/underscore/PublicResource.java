package com.enonic.wem.portal.underscore;

import java.io.File;

import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.google.common.io.Files;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.portal.base.ModuleBaseResource;

public final class PublicResource
    extends ModuleBaseResource
{
    @Override
    protected Representation get()
        throws ResourceException
    {
        final ModuleKey moduleKey = resolveModule();
        final String resourceName = getAttribute( "resource" );

        final ModuleResourceKey moduleResource = ModuleResourceKey.from( moduleKey, "public/" + resourceName );
        final File fileResource = this.modulePathResolver.resolveResourcePath( moduleResource ).toFile();
        if ( !fileResource.isFile() )
        {
            throw notFound( "File [%s] not found in module [%s]", resourceName, moduleKey.toString() );
        }

        final String ext = Files.getFileExtension( fileResource.getName() );
        final MediaType mediaType = getApplication().getMetadataService().getMediaType( ext );

        return new FileRepresentation( fileResource, mediaType );
    }
}
