package com.enonic.xp.web.handler;

import com.google.common.annotations.Beta;

@Beta
public interface WebHandler
{
    int MIN_ORDER = Integer.MIN_VALUE;

    int MAX_ORDER = Integer.MAX_VALUE;

    int getOrder();

    void handle( WebRequest req, WebResponse res, WebHandlerChain webHandlerChain );
}
