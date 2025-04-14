package com.enonic.xp.web.impl.context;

import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getContext() ).thenReturn(
            Map.of( "k", "v", "com.enonic.xp.repository.RepositoryId", "com.enonic.cms.myrepo", "com.enonic.xp.branch.Branch", "master" ) );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        filter.doFilter( req, res, chain );

        final ArgumentCaptor<HttpServletRequest> reqArg = ArgumentCaptor.forClass( HttpServletRequest.class );
        Mockito.verify( chain, Mockito.times( 1 ) ).doFilter( reqArg.capture(), Mockito.eq( res ) );
        assertSame( req, reqArg.getValue() );
    }
}
