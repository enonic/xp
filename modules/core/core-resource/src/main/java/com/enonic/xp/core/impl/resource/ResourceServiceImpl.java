package com.enonic.xp.core.impl.resource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.server.RunMode;

@Component(immediate = true)
public final class ResourceServiceImpl
    implements ResourceService, ApplicationInvalidator
{
    private ApplicationService applicationService;

    protected ResourceLoader resourceLoader;

    private final ResourceProcessorCache processorCache;

    public ResourceServiceImpl()
    {
        this.resourceLoader = createResourceLoader();
        this.processorCache = new ResourceProcessorCache( RunMode.get() != RunMode.DEV );
    }

    private ResourceLoader createResourceLoader()
    {
        if ( RunMode.get() == RunMode.DEV )
        {
            return new DevResourceLoader();
        }

        return new BundleResourceLoader();
    }

    @Override
    public Resource getResource( final ResourceKey key )
    {
        final Application app = findApplication( key.getApplicationKey() );
        if ( app == null )
        {
            return new UrlResource( key, null );
        }

        return this.resourceLoader.getResource( app, key );
    }

    @Override
    public ResourceKeys findFiles( final ApplicationKey key, final String path, final String ext, final boolean recursive )
    {
        final Application app = findApplication( key );
        if ( app == null )
        {
            return ResourceKeys.empty();
        }

        return this.resourceLoader.findFiles( app, path, ext, recursive );
    }

    @Override
    public ResourceKeys findFolders( final ApplicationKey key, final String path )
    {
        final Application app = findApplication( key );
        if ( app == null )
        {
            return ResourceKeys.empty();
        }

        return this.resourceLoader.findFolders( app, path );
    }

    private Application findApplication( final ApplicationKey key )
    {
        try
        {
            final Application application = this.applicationService.getApplication( key );
            return application.isStarted() ? application : null;
        }
        catch ( final ApplicationNotFoundException e )
        {
            return null;
        }
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.processorCache.invalidate();
    }
}
