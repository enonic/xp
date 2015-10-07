package com.enonic.xp.lib.thymeleaf;

import java.io.InputStream;

import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.resourceresolver.IResourceResolver;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.bean.BeanContext;

final class ThymeleafResourceResolver
    implements IResourceResolver
{
    private BeanContext context;

    @Override
    public String getName()
    {
        return "module";
    }

    @Override
    public InputStream getResourceAsStream( final TemplateProcessingParameters params, final String name )
    {
        final ResourceKey callingTemplate = findCallingTemplate( params );
        final ResourceKey key = resolveResource( callingTemplate, name );

        final Resource resource = resolveResource( key );
        return resource.openStream();
    }

    public void initialize( final BeanContext context )
    {
        this.context = context;
    }

    private ResourceKey resolveResource( final ResourceKey callingTemplate, final String name )
    {
        try
        {
            return ResourceKey.from( name );
        }
        catch ( final Exception e )
        {
            return resolveHtmlFragment( callingTemplate, name );
        }
    }

    private ResourceKey resolveHtmlFragment( final ResourceKey callingTemplate, final String name )
    {
        if ( !name.endsWith( ".html" ) )
        {
            return resolveHtmlFragment( callingTemplate, name + ".html" );
        }

        if ( name.startsWith( "/" ) )
        {
            return callingTemplate.resolve( name );
        }

        return callingTemplate.resolve( "../" + name );
    }

    private Resource resolveResource( final ResourceKey key )
    {
        final ResourceService resourceService = this.context.getService( ResourceService.class ).get();
        return resourceService.getResource( key );
    }

    private ResourceKey findCallingTemplate( final TemplateProcessingParameters params )
    {
        final IProcessingContext context = params.getProcessingContext();
        if ( context instanceof Arguments )
        {
            return findCallingTemplate( (Arguments) context );
        }

        return null;
    }

    private ResourceKey findCallingTemplate( final Arguments args )
    {
        return ResourceKey.from( args.getTemplateName() );
    }
}
