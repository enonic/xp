package com.enonic.xp.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.mock.MockHttpServletRequest;

public class OncePerRequestHandlerTest
{
    private OncePerRequestHandler newHandler()
        throws Exception
    {
        return Mockito.spy( new OncePerRequestHandler()
        {
            @Override
            protected boolean canHandle( final HttpServletRequest req )
            {
                return true;
            }

            @Override
            protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
                throws Exception
            {
            }
        } );
    }

    @Test
    public void testHandleOnce()
        throws Exception
    {
        final OncePerRequestHandler handler = newHandler();

        final HttpServletRequest req = new MockHttpServletRequest();
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );
        final WebHandlerChain chain = Mockito.mock( WebHandlerChain.class );

        handler.handle( req, res, chain );
        Mockito.verify( chain, Mockito.times( 0 ) ).handle( req, res );
        Mockito.verify( handler, Mockito.times( 1 ) ).doHandle( req, res, chain );
    }

    @Test
    public void testHandleTwice()
        throws Exception
    {
        final OncePerRequestHandler handler = newHandler();

        final HttpServletRequest req = new MockHttpServletRequest();
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );
        final WebHandlerChain chain = Mockito.mock( WebHandlerChain.class );

        handler.handle( req, res, chain );
        Mockito.verify( chain, Mockito.times( 0 ) ).handle( req, res );
        Mockito.verify( handler, Mockito.times( 1 ) ).doHandle( req, res, chain );

        Mockito.reset( chain );
        Mockito.reset( handler );

        handler.handle( req, res, chain );
        Mockito.verify( chain, Mockito.times( 1 ) ).handle( req, res );
        Mockito.verify( handler, Mockito.times( 0 ) ).doHandle( req, res, chain );
    }
}
