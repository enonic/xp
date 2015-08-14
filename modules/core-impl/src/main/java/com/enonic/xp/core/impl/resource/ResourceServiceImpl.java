package com.enonic.xp.core.impl.resource;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;

@Component(immediate = true)
public class ResourceServiceImpl
    implements ResourceService
{

    private ApplicationService applicationService;

    @Override
    public Resource getResource( final ResourceKey resourceKey )
    {
        URL resourceUrl = null;
        final Application application = getActiveApplication( resourceKey.getApplicationKey() );
        if ( application != null )
        {
            String resourcePath = resourceKey.getPath();
            resourceUrl = application.getBundle().getResource( resourcePath );
        }

        return new UrlResource( resourceKey, resourceUrl );
    }

    @Override
    public ResourceKeys findResourceKeys( final ApplicationKey applicationKey, final String path, final String filePattern,
                                          boolean recurse )
    {
        ResourceKeys resourceKeys = null;
        final Application application = getActiveApplication( applicationKey );

        if ( application != null )
        {
            Bundle bundle = application.getBundle();

            final Enumeration<URL> entries = bundle.findEntries( path, filePattern, recurse );

            if ( entries != null )
            {
                final List<ResourceKey> resourceKeyList = Collections.list( entries ).
                    stream().
                    map( resourceUrl -> ResourceKey.from( applicationKey, resourceUrl.getPath() ) ).
                    collect( Collectors.toList() );

                resourceKeys = ResourceKeys.from( resourceKeyList );
            }

        }

        if ( resourceKeys == null )
        {
            resourceKeys = ResourceKeys.empty();
        }

        return resourceKeys;
    }

    private Application getActiveApplication( final ApplicationKey applicationKey )
    {
        Application activeApplication = null;

        final Application application = applicationService.getApplication( applicationKey );
        if ( application != null && application.getBundle().getState() == Bundle.ACTIVE )
        {
            activeApplication = application;
        }

        return activeApplication;
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
