package com.enonic.xp.web.impl.context;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class ContextFilterTest
{
    @Test
    void testHandle()
        throws Exception
    {
        final ContextFilter filter = new ContextFilter();

        assertNull( ContextAccessor.current().getLocalScope().getSession() );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        filter.doFilter( req, res, chain );

        final ArgumentCaptor<HttpServletRequest> reqArg = ArgumentCaptor.forClass( HttpServletRequest.class );
        Mockito.verify( chain, Mockito.times( 1 ) ).doFilter( reqArg.capture(), Mockito.eq( res ) );
        assertSame( req, reqArg.getValue() );
    }
}
