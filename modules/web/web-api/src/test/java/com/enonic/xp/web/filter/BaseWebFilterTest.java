package com.enonic.xp.web.filter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class BaseWebFilterTest
{
    private BaseWebFilter newFilter()
    {
        return Mockito.spy( new BaseWebFilter()
        {
            @Override
            public void doFilter( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
            {
            }
        } );
    }

    @Test
    void testLifecycle()
        throws Exception
    {
        final BaseWebFilter filter = newFilter();
        filter.init( Mockito.mock( FilterConfig.class ) );
        filter.destroy();
    }

    @Test
    void testFilter()
        throws Exception
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );
        final FilterChain chain = Mockito.mock( FilterChain.class );

        final BaseWebFilter filter = newFilter();
        filter.doFilter( req, res, chain );
    }
}
