package com.enonic.xp.web.handler;

import com.google.common.annotations.Beta;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Beta
public interface WebHandler
{
    int MIN_ORDER = Integer.MIN_VALUE;

    int MAX_ORDER = Integer.MAX_VALUE;

    int getOrder();

    WebResponse handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception;
}