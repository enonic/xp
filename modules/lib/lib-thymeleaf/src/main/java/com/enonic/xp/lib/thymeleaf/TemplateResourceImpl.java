package com.enonic.xp.lib.thymeleaf;

import java.io.IOException;
import java.io.Reader;

import org.thymeleaf.templateresource.ITemplateResource;

import com.google.common.io.Files;

import com.enonic.xp.resource.Resource;

final class TemplateResourceImpl
    implements ITemplateResource
{
    private final Resource resource;

    private final TemplateResourceResolver resolver;

    TemplateResourceImpl( final Resource resource, final TemplateResourceResolver resolver )
    {
        this.resource = resource;
        this.resolver = resolver;
    }

    @Override
    public String getDescription()
    {
        return this.resource.getKey().toString();
    }

    @Override
    public String getBaseName()
    {
        return Files.getNameWithoutExtension( this.resource.getKey().getName() );
    }

    @Override
    public boolean exists()
    {
        return this.resource.exists();
    }

    @Override
    public Reader reader()
        throws IOException
    {
        return this.resource.openReader();
    }

    @Override
    public ITemplateResource relative( final String relativeLocation )
    {
        return this.resolver.resolve( this.resource.getKey(), relativeLocation );
    }
}
