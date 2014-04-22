package com.enonic.wem.api.resource;

import java.net.URL;

public final class ResourceUrl
    implements ResourceReference
{
    private final URL url;

    private ResourceUrl( final URL url )
    {
        this.url = url;
    }

    @Override
    public String getUri()
    {
        return this.url.toString();
    }

    public URL getUrl()
    {
        return this.url;
    }

    @Override
    public String toString()
    {
        return getUri();
    }

    public static ResourceUrl from( final URL url )
    {
        return new ResourceUrl( url );
    }
}
