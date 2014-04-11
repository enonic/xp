package com.enonic.wem.core.resource;

import java.net.URL;

import com.enonic.wem.api.resource.Resource2;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;

final class SystemResourceResolver
    implements ResourceResolver
{
    private final static String PREFIX = "system";

    private ClassLoader classLoader;

    @Override
    public Resource2 resolve( final ResourceKey key )
    {
        final String path = getPath( key );
        final URL url = this.classLoader.getResource( path );

        if ( url == null )
        {
            return null;
        }

        return new Resource2Impl( key, url );
    }

    @Override
    public ResourceKeys getChildren( final ResourceKey parentKey )
    {
        throw new UnsupportedOperationException();
    }

    private String getPath( final ResourceKey key )
    {
        return PREFIX + key.getPath();
    }

    public SystemResourceResolver classLoader( final ClassLoader classLoader )
    {
        this.classLoader = classLoader;
        return this;
    }
}
