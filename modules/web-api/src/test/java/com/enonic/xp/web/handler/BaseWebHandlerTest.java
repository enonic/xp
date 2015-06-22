package com.enonic.xp.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class BaseWebHandlerTest
{
    @Test
    public void testOrder()
    {
        final BaseWebHandler context = Mockito.mock( BaseWebHandler.class, Mockito.CALLS_REAL_METHODS );
        Assert.assertEquals( 0, context.getOrder() );

        context.setOrder( 100 );
        Assert.assertEquals( 100, context.getOrder() );
    }

    private BaseWebHandler newHandler( final boolean canHandle )
        throws Exception
    {
        return Mockito.spy( new BaseWebHandler()
        {
            @Override
            protected boolean canHandle( final HttpServletRequest req )
            {
                return canHandle;
            }

            @Override
            protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
                throws Exception
            {
            }
        } );
    }

    @Test
    public void testHandled()
        throws Exception
    {
        final BaseWebHandler handler = newHandler( true );

        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );
        final WebHandlerChain chain = Mockito.mock( WebHandlerChain.class );

        handler.handle( req, res, chain );
        Mockito.verify( chain, Mockito.times( 0 ) ).handle( req, res );
        Mockito.verify( handler, Mockito.times( 1 ) ).doHandle( req, res, chain );
    }

    @Test
    public void testNotHandled()
        throws Exception
    {
        final BaseWebHandler handler = newHandler( false );

        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );
        final WebHandlerChain chain = Mockito.mock( WebHandlerChain.class );

        handler.handle( req, res, chain );
        Mockito.verify( chain, Mockito.times( 1 ) ).handle( req, res );
        Mockito.verify( handler, Mockito.times( 0 ) ).doHandle( req, res, chain );
    }
}
