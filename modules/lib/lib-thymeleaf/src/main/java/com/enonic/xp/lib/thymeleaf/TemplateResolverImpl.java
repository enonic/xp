package com.enonic.xp.lib.thymeleaf;

import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.server.RunMode;

final class TemplateResolverImpl
    extends AbstractConfigurableTemplateResolver
    implements TemplateResourceResolver
{
    private BeanContext context;

    private boolean disableCache;

    TemplateResolverImpl( final BeanContext context )
    {
        this.context = context;
        this.disableCache = RunMode.get() == RunMode.DEV;
    }

    @Override
    protected ITemplateResource computeTemplateResource( final IEngineConfiguration configuration, final String ownerTemplate,
                                                         final String template, final String resourceName, final String characterEncoding,
                                                         final Map<String, Object> templateResolutionAttributes )
    {
        return resolve( this.context.getResourceKey(), ownerTemplate, resourceName );
    }

    @Override
    protected ICacheEntryValidity computeValidity( final IEngineConfiguration configuration, final String ownerTemplate,
                                                   final String template, final Map<String, Object> templateResolutionAttributes )
    {
        if ( disableCache )
        {
            return new NonCacheableCacheEntryValidity();
        }
        return super.computeValidity( configuration, ownerTemplate, template, templateResolutionAttributes );
    }

    private ITemplateResource resolve( final ResourceKey base, final String ownerTempalte, final String location )
    {
        final ResourceKey parent = ownerTempalte != null ? ResourceKey.from( ownerTempalte ) : base;
        return resolve( parent, location );
    }

    @Override
    public ITemplateResource resolve( final ResourceKey base, final String location )
    {
        final ResourceKey resolved = resolveKey( base, location );
        final Resource resource = resolveResource( resolved );
        return new TemplateResourceImpl( resource, this );
    }

    private ResourceKey resolveKey( final ResourceKey base, final String location )
    {
        try
        {
            return ResourceKey.from( location );
        }
        catch ( final Exception e )
        {
            return doResolveResource( base, location );
        }
    }

    private ResourceKey doResolveResource( final ResourceKey callingTemplate, final String name )
    {
        if ( name.startsWith( "/" ) )
        {
            return doResolveResource( callingTemplate.resolve( name ) );
        }

        return doResolveResource( callingTemplate.resolve( "../" + name ) );
    }

    private ResourceKey doResolveResource( final ResourceKey key )
    {
        if ( key.getName().contains( "." ) )
        {
            return key;
        }

        return ResourceKey.from( key.getUri() + ".html" );
    }

    private Resource resolveResource( final ResourceKey key )
    {
        final ResourceService resourceService = this.context.getService( ResourceService.class ).get();
        return resourceService.getResource( key );
    }
}
