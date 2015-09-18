package com.enonic.xp.web.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

public final class TestWebHandler
    implements WebHandler
{
    private final int order;

    private final boolean handleNext;

    private int invocations;

    public TestWebHandler( final int order, final boolean handleNext )
    {
        this.order = order;
        this.handleNext = handleNext;
        this.invocations = 0;
    }

    public int getInvocations()
    {
        return this.invocations;
    }

    @Override
    public int getOrder()
    {
        return this.order;
    }

    @Override
    public void handle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        this.invocations++;

        if ( this.handleNext )
        {
            chain.handle( req, res );
            return;
        }

        res.getWriter().print( "handled" );
    }
}
