package com.enonic.xp.web.handler;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@PublicApi
public interface WebHandler
{
    int MIN_ORDER = Integer.MIN_VALUE;

    int MAX_ORDER = Integer.MAX_VALUE;

    int getOrder();

    WebResponse handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception;
}