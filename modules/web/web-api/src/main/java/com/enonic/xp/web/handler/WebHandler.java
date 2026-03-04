package com.enonic.xp.web.handler;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;


public interface WebHandler
{
    int MIN_ORDER = Integer.MIN_VALUE;

    int MAX_ORDER = Integer.MAX_VALUE;

    int getOrder();

    WebResponse handle( WebRequest webRequest, WebResponse webResponse, WebHandlerChain webHandlerChain )
        throws Exception;
}
