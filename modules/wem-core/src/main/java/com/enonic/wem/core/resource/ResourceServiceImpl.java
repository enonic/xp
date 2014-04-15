package com.enonic.wem.core.resource;

import javax.inject.Inject;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;
import com.enonic.wem.core.config.SystemConfig;

public final class ResourceServiceImpl
    extends AbstractResourceService
{
    private final ClassLoader classLoader;

    protected final SystemConfig systemConfig;

    @Inject
    public ResourceServiceImpl( final SystemConfig systemConfig )
    {
        this( systemConfig, null );
    }

    public ResourceServiceImpl( final SystemConfig systemConfig, final ClassLoader classLoader )
    {
        this.systemConfig = systemConfig;
        this.classLoader = classLoader != null ? classLoader : this.getClass().getClassLoader();
    }

    private ResourceResolver createResolver( final ResourceKey key )
    {
        if ( key.getModule().isSystem() )
        {
            return new SystemResourceResolver().classLoader( this.classLoader );
        }
        else
        {
            return new ModuleResourceResolver().systemConfig( this.systemConfig );
        }
    }


    @Override
    public ResourceKeys getChildren( final ResourceKey parent )
    {
        return createResolver( parent ).getChildren( parent );
    }

    @Override
    protected Resource resolve( final ResourceKey key )
    {
        return createResolver( key ).resolve( key );
    }
}
