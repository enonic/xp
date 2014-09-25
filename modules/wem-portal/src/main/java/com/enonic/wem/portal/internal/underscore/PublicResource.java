package com.enonic.wem.portal.internal.underscore;

import java.io.IOException;
import java.net.URL;

import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.google.common.base.Throwables;
import com.google.common.io.Files;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.portal.internal.base.ModuleBaseResource;

public final class PublicResource
    extends ModuleBaseResource
{
    @Override
    protected Representation get()
        throws ResourceException
    {
        final ModuleKey moduleKey = resolveModule();
        final String resourceName = getAttribute( "resource" );

        final Module module = moduleService.getModule( moduleKey );
        if ( module == null )
        {
            throw notFound( "Module [%s] not found", moduleKey );
        }
        final URL resourceUrl = module.getResource( "public/" + resourceName );
        if ( resourceUrl == null )
        {
            throw notFound( "File [%s] not found in module [%s]", resourceName, moduleKey.toString() );
        }

        final String ext = Files.getFileExtension( resourceName );
        final MediaType mediaType = getApplication().getMetadataService().getMediaType( ext );

        try
        {
            return new InputRepresentation( resourceUrl.openStream(), mediaType );
        }
        catch ( IOException e )
        {
            throw Throwables.propagate( e );
        }
    }
}
