package com.enonic.xp.lib.thymeleaf;

import java.io.InputStream;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

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
    public InputStream getResourceAsStream( final TemplateProcessingParameters params, final String resourceName )
    {
        final ResourceKey resourceKey = ResourceKey.from( resourceName );

        try
        {
            final ResourceService resourceService = context.getService( ResourceService.class ).get();
            final Resource resource = resourceService.getResource( resourceKey );
            return resource.getUrl().openStream();
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    public void initialize( final BeanContext context )
    {
        this.context = context;
    }
}
