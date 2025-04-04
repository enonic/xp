package com.enonic.xp.web.impl.dispatch.mapping;

import java.io.IOException;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ServletDefinition
    extends ResourceDefinition<Servlet>
{
    boolean service( HttpServletRequest req, HttpServletResponse res )
        throws IOException, ServletException;
}
