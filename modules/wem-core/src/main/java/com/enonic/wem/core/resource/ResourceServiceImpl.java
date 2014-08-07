package com.enonic.wem.core.resource;

import java.net.URL;

import com.enonic.wem.api.resource.Resource2;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.api.resource.ResourceUrlResolver;

public final class ResourceServiceImpl
    implements ResourceService
{
    @Override
    public Resource2 getResource2( final ResourceKey key )
    {
        final URL url = ResourceUrlResolver.resolve( key );
        return new Resource2Impl( key, url );
    }
}
