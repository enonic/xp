package com.enonic.wem.portal.filters.request;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class MyRequestFilterTest
    implements ContainerRequestFilter
{

    @Override
    public ContainerRequest filter( final ContainerRequest request )
    {
        return request;
    }
}
