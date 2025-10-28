package com.enonic.xp.web.impl.handler;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

class TestWebHandler
    implements WebHandler
{
    protected WebResponse response;

    protected RequestVerifier verifier = req -> {
    };

    private final int order;

    public TestWebHandler()
    {
        this( 0 );
    }

    public TestWebHandler( final int order )
    {
        this.order = order;
    }

    @Override
    public int getOrder()
    {
        return order;
    }

    @Override
    public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        this.verifier.verify( webRequest );
        return this.response;
    }
}
