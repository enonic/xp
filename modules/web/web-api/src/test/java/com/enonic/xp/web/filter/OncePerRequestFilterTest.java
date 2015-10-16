package com.enonic.xp.web.filter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

public class OncePerRequestFilterTest
{
    private OncePerRequestFilter newFiler()
        throws Exception
    {
        return Mockito.spy( new OncePerRequestFilter()
        {
            @Override
            protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
                throws Exception
            {
            }
        } );
    }

    @Test
    public void testHandleOnce()
        throws Exception
    {
        final OncePerRequestFilter filter = newFiler();

        final HttpServletRequest req = new MockHttpServletRequest();
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );
        final FilterChain chain = Mockito.mock( FilterChain.class );

        filter.doFilter( req, res, chain );
        Mockito.verify( chain, Mockito.times( 0 ) ).doFilter( req, res );
        Mockito.verify( filter, Mockito.times( 1 ) ).doHandle( req, res, chain );
    }

    @Test
    public void testHandleTwice()
        throws Exception
    {
        final OncePerRequestFilter filter = newFiler();

        final HttpServletRequest req = new MockHttpServletRequest();
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );
        final FilterChain chain = Mockito.mock( FilterChain.class );

        filter.doFilter( req, res, chain );
        Mockito.verify( chain, Mockito.times( 0 ) ).doFilter( req, res );
        Mockito.verify( filter, Mockito.times( 1 ) ).doHandle( req, res, chain );

        Mockito.reset( chain );
        Mockito.reset( filter );

        filter.doFilter( req, res, chain );
        Mockito.verify( chain, Mockito.times( 1 ) ).doFilter( req, res );
        Mockito.verify( filter, Mockito.times( 0 ) ).doHandle( req, res, chain );
    }
}