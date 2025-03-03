package com.enonic.xp.web.filter;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        filter.doFilter( req, res, chain );
        verify( chain, Mockito.times( 0 ) ).doFilter( req, res );
        verify( filter, Mockito.times( 1 ) ).doHandle( req, res, chain );
    }

    @Test
    public void testHandleTwice()
        throws Exception
    {
        final OncePerRequestFilter filter = newFiler();

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        filter.doFilter( req, res, chain );
        verify( chain, Mockito.times( 0 ) ).doFilter( req, res );
        verify( filter, Mockito.times( 1 ) ).doHandle( req, res, chain );

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass( String.class );

        verify( req ).setAttribute( captor.capture(), eq( Boolean.TRUE ) );

        final String attributeName = captor.getValue();
        reset( chain );
        reset( filter );

        when( req.getAttribute( attributeName ) ).thenReturn( Boolean.TRUE );
        filter.doFilter( req, res, chain );
        verify( chain, Mockito.times( 1 ) ).doFilter( req, res );
        verify( filter, Mockito.times( 0 ) ).doHandle( req, res, chain );
    }
}
