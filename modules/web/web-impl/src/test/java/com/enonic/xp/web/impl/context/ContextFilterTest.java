package com.enonic.xp.web.impl.context;

import javax.servlet.FilterChain;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.context.ContextAccessor;

public class ContextFilterTest
{
    @Test
    public void testHandle()
        throws Exception
    {
        final ContextFilter filter = new ContextFilter();

        Assert.assertNull( ContextAccessor.current().getLocalScope().getSession() );

        final MockHttpServletRequest req = new MockHttpServletRequest();
        final MockHttpServletResponse res = new MockHttpServletResponse();
        final FilterChain chain = Mockito.mock( FilterChain.class );

        filter.doFilter( req, res, chain );

        Mockito.verify( chain, Mockito.times( 1 ) ).doFilter( Mockito.eq( req ), Mockito.eq( res ) );
    }
}
