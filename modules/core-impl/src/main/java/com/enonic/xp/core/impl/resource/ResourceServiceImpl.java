package com.enonic.xp.core.impl.resource;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
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
        final Module module = getActiveModule( resourceKey.getModule() );
        if ( module != null )
        {
            resource = getResource( module.getBundle(), resourceKey );
        }
        return resource;
    }

    @Override
    public Resources findResources( final ModuleKey moduleKey, final String pattern )
    {
        Resources resources = null;
        final Module module = getActiveModule( moduleKey );

        if ( module != null )
        {
            Bundle bundle = module.getBundle();
            final Pattern compiledPattern = Pattern.compile( pattern );
            final Enumeration<String> entryPaths = bundle.getEntryPaths( "/" );

            final List<Resource> resourceList = Collections.list( entryPaths ).
                stream().
                filter( resourcePath -> compiledPattern.matcher( resourcePath ).matches() ).
                map( resourcePath -> getResource( bundle, ResourceKey.from( module.getKey(), resourcePath ) ) ).
                collect( Collectors.toList() );

            resources = Resources.from( resourceList );
        }

        if ( resources == null )
        {
            resources = Resources.empty();
        }

        return resources;
    }

    private Module getActiveModule( ModuleKey moduleKey )
    {
        Module activeModule = null;

        final Module module = moduleService.getModule( moduleKey );
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
