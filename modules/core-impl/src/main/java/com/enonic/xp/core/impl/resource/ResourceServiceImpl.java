package com.enonic.xp.core.impl.resource;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        final Module module = moduleService.getModule( resourceKey.getModule() );
        if ( module != null )
        {
            final URL resourceUrl = module.getResource( resourceKey.getPath() );
            if ( resourceUrl != null )
            {
                resource = new Resource( resourceKey, resourceUrl );
            }
        }
        return resource;
    }

    @Override
    public Resources findResources( final ModuleKey moduleKey, final String pattern )
    {
        Resources resources = null;
        final Module module = moduleService.getModule( moduleKey );
        if ( module != null )
        {
            final Set<String> resourcePaths = module.getResourcePaths();
            final Pattern compiledPattern = Pattern.compile( pattern );

            final List<Resource> resourceList = resourcePaths.stream().
                filter( resourcePath -> compiledPattern.matcher( resourcePath ).matches() ).
                map( resourcePath -> getResource( module, resourcePath ) ).
                collect( Collectors.toList() );

            resources = Resources.from( resourceList );
        }

        if ( resources == null )
        {
            resources = Resources.empty();
        }

        return resources;
    }

    private static Resource getResource( final Module module, final String resourcePath )
    {
        final ResourceKey resourceKey = ResourceKey.from( module.getKey(), resourcePath );
        final URL resourceUrl = module.getResource( resourcePath );
        return new Resource( resourceKey, resourceUrl );
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
