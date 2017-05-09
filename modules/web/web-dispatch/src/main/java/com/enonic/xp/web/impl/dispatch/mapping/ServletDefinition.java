package com.enonic.xp.web.impl.dispatch.mapping;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletDefinition
    extends ResourceDefinition<Servlet>
{
    boolean service( HttpServletRequest req, HttpServletResponse res )
        throws IOException, ServletException;
}
