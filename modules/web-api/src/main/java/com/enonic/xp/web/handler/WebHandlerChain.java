package com.enonic.xp.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebHandlerChain
{
    public void handle( HttpServletRequest req, HttpServletResponse res )
        throws Exception;
}
