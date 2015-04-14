package com.enonic.xp.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.annotations.Beta;

@Beta
public interface WebHandlerChain
{
    void handle( HttpServletRequest req, HttpServletResponse res )
        throws Exception;
}
