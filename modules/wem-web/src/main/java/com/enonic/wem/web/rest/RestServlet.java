package com.enonic.wem.web.rest;

import org.springframework.web.servlet.DispatcherServlet;

public final class RestServlet
    extends DispatcherServlet
{
    public RestServlet()
    {
        setContextConfigLocation( "" );
    }
}
