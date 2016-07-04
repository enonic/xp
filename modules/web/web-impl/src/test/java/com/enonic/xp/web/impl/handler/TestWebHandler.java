package com.enonic.xp.web.impl.handler;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

public final class TestWebHandler
    implements WebHandler
{
    protected WebResponse response;

    protected RequestVerifier verifier = req -> {
    };

    @Override
    public int getOrder()
    {
        return 0;
    }

    @Override
    public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        this.verifier.verify( webRequest );
        return this.response;
    }
}
