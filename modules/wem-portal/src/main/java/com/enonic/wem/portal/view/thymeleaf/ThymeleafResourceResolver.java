package com.enonic.wem.portal.view.thymeleaf;

import java.io.InputStream;
import java.net.URL;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleResourceUrlResolver;

final class ThymeleafResourceResolver
    implements IResourceResolver
{
    public ThymeleafResourceResolver()
    {
    }

    @Override
    public String getName()
    {
        return "module";
    }

    @Override
    public InputStream getResourceAsStream( final TemplateProcessingParameters params, final String resourceName )
    {
        final ModuleResourceKey key = ModuleResourceKey.from( resourceName );
        final URL resourceUrl = ModuleResourceUrlResolver.resolve( key);

        try
        {
            return resourceUrl.openStream();
        }
        catch ( final Exception e )
        {
            return null;
        }
    }
}
