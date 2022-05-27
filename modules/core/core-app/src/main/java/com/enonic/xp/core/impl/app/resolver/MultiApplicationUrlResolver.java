package com.enonic.xp.core.impl.app.resolver;

import java.util.HashSet;
import java.util.Set;

import com.enonic.xp.issue.VirtualAppConstants;
import com.enonic.xp.resource.Resource;

public final class MultiApplicationUrlResolver
    implements ApplicationUrlResolver
{
    private final ApplicationUrlResolver[] list;

    public MultiApplicationUrlResolver( final ApplicationUrlResolver... list )
    {
        this.list = list;
    }

    @Override
    public Set<String> findFiles()
    {
        final Set<String> set = new HashSet<>();
        for ( final ApplicationUrlResolver resolver : this.list )
        {
            set.addAll( resolver.findFiles() );
        }

        return set;
    }

    @Override
    public Resource findResource( final String path )
    {
        Resource resourceToReturn = null;

        for ( final ApplicationUrlResolver resolver : this.list )
        {
            final Resource resource = resolver.findResource( path );
            if ( resource != null )
            {
                resourceToReturn = resource;

                if ( !resourceToReturn.isVirtual() || !VirtualAppConstants.SITE_RESOURCE_PATH.equals( path ) ||
                    !VirtualAppConstants.DEFAULT_SITE_RESOURCE_VALUE.equals( resourceToReturn.getBytes() ) )
                {
                    return resourceToReturn;
                }
            }
        }

        return resourceToReturn;
    }
}
