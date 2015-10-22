package com.enonic.xp.web.jmx.impl;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jolokia.http.AgentServlet;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = Servlet.class,
    property = {"osgi.http.whiteboard.servlet.pattern=/jmx", "osgi.http.whiteboard.servlet.pattern=/jmx/*"})
public final class JmxServlet
    extends AgentServlet
{
    public JmxServlet()
    {
        super( new RestrictorImpl() );
    }

    @Override
    public void init( final ServletConfig config )
        throws ServletException
    {
        super.init( new JmxServletConfig( config ) );
    }
}
