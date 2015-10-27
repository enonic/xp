package com.enonic.xp.web.filter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;

public class BaseWebFilterTest
{
    private BaseWebFilter newFilter()
    {
        return Mockito.spy( new BaseWebFilter()
        {
            @Override
            public void doFilter( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
                throws Exception
            {
            }
        } );
    }

    @Test
    public void testLifecycle()
        throws Exception
    {
        final BaseWebFilter filter = newFilter();
        filter.init( Mockito.mock( FilterConfig.class ) );
        filter.destroy();
    }

    @Test
    public void testFilter()
        throws Exception
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );
        final FilterChain chain = Mockito.mock( FilterChain.class );

        final BaseWebFilter filter = newFilter();
        filter.doFilter( req, res, chain );
    }
}