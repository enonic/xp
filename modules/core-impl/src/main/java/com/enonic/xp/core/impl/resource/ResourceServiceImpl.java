package com.enonic.xp.core.impl.resource;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.Resources;

@Component(immediate = true)
public class ResourceServiceImpl
    implements ResourceService
{

    private ModuleService moduleService;

    @Override
    public Resource getResource( final ResourceKey resourceKey )
    {
        Resource resource = null;
        final Module module = getActiveModule( resourceKey.getApplicationKey() );
        if ( module != null )
        {
            resource = getResource( module.getBundle(), resourceKey );
        }
        return resource;
    }

    @Override
    public Resources findResources( final ApplicationKey applicationKey, final String path, final String filePattern )
    {
        Resources resources = null;
        final Module module = getActiveModule( applicationKey );

        if ( module != null )
        {
            Bundle bundle = module.getBundle();

            final Enumeration<URL> entries = bundle.findEntries( path, filePattern, true );

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

    private Module getActiveModule( ApplicationKey applicationKey )
    {
        Module activeModule = null;

        final Module module = moduleService.getModule( applicationKey );
        if ( module != null && module.getBundle().getState() == Bundle.ACTIVE )
        {
            activeModule = module;
        }

        return activeModule;
    }

    private Resource getResource( Bundle bundle, ResourceKey resourceKey )
    {
        Resource resource = null;

        final URL resourceUrl = bundle.getResource( resourceKey.getPath() );
        if ( resourceUrl != null )
        {
            resource = new Resource( resourceKey, resourceUrl );
        }

        return resource;
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
