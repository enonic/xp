package com.enonic.xp.web.impl.context;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.mock.MockHttpServletRequest;
import com.enonic.xp.web.mock.MockHttpServletResponse;

public class ContextHandlerTest
{
    @Test
    public void testHandle()
        throws Exception
    {
        final ContextHandler handler = new ContextHandler();

        Assert.assertNull( ContextAccessor.current().getLocalScope().getSession() );

        final MockHttpServletRequest req = new MockHttpServletRequest();
        final MockHttpServletResponse res = new MockHttpServletResponse();
        final WebHandlerChain chain = Mockito.mock( WebHandlerChain.class );

        handler.handle( req, res, chain );

        final ArgumentCaptor<HttpServletRequest> reqArg = ArgumentCaptor.forClass( HttpServletRequest.class );
        Mockito.verify( chain, Mockito.times( 1 ) ).handle( reqArg.capture(), Mockito.eq( res ) );
        Assert.assertEquals( HttpRequestDelegate.class, reqArg.getValue().getClass() );
    }
}
