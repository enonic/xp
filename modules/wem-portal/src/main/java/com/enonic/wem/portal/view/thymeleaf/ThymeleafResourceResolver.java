package com.enonic.wem.portal.view.thymeleaf;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.core.module.ModuleResourcePathResolver;

final class ThymeleafResourceResolver
    implements IResourceResolver
{
    private final ModuleResourcePathResolver pathResolver;

    public ThymeleafResourceResolver( final ModuleResourcePathResolver pathResolver )
    {
        this.pathResolver = pathResolver;
    }

    @Override
    public String getName()
    {
        return "module";
    }

    @Override
    public InputStream getResourceAsStream( final TemplateProcessingParameters params, final String resourceName )
    {
        final ResourceKey key = ResourceKey.from( resourceName );
        final Path path = this.pathResolver.resolveResourcePath( key );

        if ( !Files.isRegularFile( path ) )
        {
            return null;
        }

        try
        {
            return new FileInputStream( path.toFile() );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }
}
