package com.enonic.wem.core.resource;

import java.net.URL;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.api.resource.ResourceUrlResolver;

public final class ResourceServiceImpl
    implements ResourceService
{
    @Override
    public Resource getResource( final ResourceKey key )
    {
        final URL url = ResourceUrlResolver.resolve( key );
        return new ResourceImpl( key, url );
    }
}
