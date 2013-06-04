package com.enonic.wem.portal.filters.response;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class MyResponseFilterTest
    implements ContainerResponseFilter
{
    @Override
    public ContainerResponse filter( final ContainerRequest containerRequest, final ContainerResponse containerResponse )
    {
        return containerResponse;
    }
}
