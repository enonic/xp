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
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.ResourceUrlResolver;
import com.enonic.xp.resource.Resources;

@Component(immediate = true)
public class ResourceServiceImpl
    implements ResourceService
{

    private ApplicationService applicationService;

    @Override
    public Resource getResource( final ResourceKey resourceKey )
    {
        Resource resource = null;
        final Application application = getActiveApplication( resourceKey.getApplicationKey() );
        if ( application != null )
        {
            resource = getResource( application.getBundle(), resourceKey );
        }

        if ( resource == null )
        {
            return buildResourceFromKey( resourceKey );
        }

        return resource;
    }

    @Override
    public Resources findResources( final ApplicationKey applicationKey, final String path, final String filePattern, boolean recurse )
    {
        Resources resources = null;
        final Application application = getActiveApplication( applicationKey );

        if ( application != null )
        {
            Bundle bundle = application.getBundle();

            final Enumeration<URL> entries = bundle.findEntries( path, filePattern, recurse );

            if ( entries != null )
            {
                final List<Resource> resourceList = Collections.list( entries ).
                    stream().
                    map( resourceUrl -> new Resource( ResourceKey.from( applicationKey, resourceUrl.getPath() ), resourceUrl ) ).
                    collect( Collectors.toList() );

                resources = Resources.from( resourceList );
            }

        }

        if ( resources == null )
        {
            resources = Resources.empty();
        }

        return resources;
    }

    private Application getActiveApplication( final ApplicationKey applicationKey )
    {
        Application activeApplication = null;

        final Application application = applicationService.getModule( applicationKey );
        if ( application != null && application.getBundle().getState() == Bundle.ACTIVE )
        {
            activeApplication = application;
        }

        return activeApplication;
    }

    private Resource getResource( final Bundle bundle, final ResourceKey resourceKey )
    {
        Resource resource = null;

        final URL resourceUrl = bundle.getResource( resourceKey.getPath() );
        if ( resourceUrl != null )
        {
            resource = new Resource( resourceKey, resourceUrl );
        }

        return resource;
    }

    private Resource buildResourceFromKey( final ResourceKey key )
    {
        final URL url = ResourceUrlResolver.resolve( key );
        return new Resource( key, url );
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
