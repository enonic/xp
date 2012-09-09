package com.enonic.wem.web.rest.filter;

import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

@Component
public final class AllowOriginFilter
    implements ContainerResponseFilter
{
    @Override
    public ContainerResponse filter( final ContainerRequest req, final ContainerResponse res )
    {
        res.getHttpHeaders().putSingle( "Access-Control-Allow-Origin", "*" );
        return res;
    }
}
