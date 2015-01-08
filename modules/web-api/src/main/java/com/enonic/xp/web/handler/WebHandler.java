package com.enonic.xp.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebHandler
{
    public final static int MIN_ORDER = Integer.MIN_VALUE;

    public final static int MAX_ORDER = Integer.MAX_VALUE;

    public int getOrder();

    public void handle( HttpServletRequest req, HttpServletResponse res, WebHandlerChain chain )
        throws Exception;
}
