package com.enonic.xp.web.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.mock.MockHttpServletRequest;
import com.enonic.xp.web.mock.MockHttpServletResponse;

public class WebHandlerChainImplTest
{
    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    @Before
    public void setup()
        throws Exception
    {
        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();
    }

    @Test
    public void testHandleNext_noHandlers()
        throws Exception
    {
        final WebHandlerChain chain = newChain();
        chain.handle( this.req, this.res );
    }

    @Test
    public void testHandleNext_oneHandler()
        throws Exception
    {
        final TestWebHandler handler = new TestWebHandler( 0, true );
        final WebHandlerChain chain = newChain( handler );

        chain.handle( this.req, this.res );

        Assert.assertEquals( 1, handler.getInvocations() );
    }

    @Test
    public void testHandleNext_twoHandlers()
        throws Exception
    {
        final TestWebHandler handler1 = new TestWebHandler( 0, true );
        final TestWebHandler handler2 = new TestWebHandler( 0, true );
        final WebHandlerChain chain = newChain( handler1, handler2 );

        chain.handle( this.req, this.res );

        Assert.assertEquals( 1, handler1.getInvocations() );
        Assert.assertEquals( 1, handler2.getInvocations() );
    }


    private WebHandlerChain newChain( final WebHandler... handlers )
    {
        return new WebHandlerChainImpl( ImmutableList.copyOf( handlers ) );
    }
}
