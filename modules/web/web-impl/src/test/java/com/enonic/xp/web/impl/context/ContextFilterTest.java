package com.enonic.xp.web.impl.context;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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

        final ArgumentCaptor<HttpServletRequest> reqArg = ArgumentCaptor.forClass( HttpServletRequest.class );
        Mockito.verify( chain, Mockito.times( 1 ) ).doFilter( reqArg.capture(), Mockito.eq( res ) );
        Assert.assertEquals( HttpRequestDelegate.class, reqArg.getValue().getClass() );
    }
}
