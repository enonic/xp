package com.enonic.xp.web.server;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.dispatch.ServletMapping;

public interface WebServer
{
    void start()
        throws Exception;

    void stop()
        throws Exception;

    boolean isRunning();

    int getPort();

    void addFilter( Filter filter );

    void removeFilter( Filter filter );

    void addMapping( FilterMapping mapping );

    void removeMapping( FilterMapping mapping );

    void addServlet( Servlet servlet );

    void removeServlet( Servlet servlet );

    void addMapping( ServletMapping mapping );

    void removeMapping( ServletMapping mapping );
}
