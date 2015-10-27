package com.enonic.xp.web.jetty.impl;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import org.junit.After;
import org.junit.Before;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public abstract class JettyTestSupport
{
    protected JettyTestServer server;

    protected OkHttpClient client;

    protected String baseUrl;

    @Before
    public final void startServer()
        throws Exception
    {
        this.server = new JettyTestServer();
        this.server.start();
        configure();

        this.client = new OkHttpClient();
        this.baseUrl = "http://localhost:" + this.server.getPort();
    }

    protected abstract void configure()
        throws Exception;

    @After
    public final void stopServer()
        throws Exception
    {
        this.server.stop();
    }

    protected final void addFilter( final Filter filter, final String mapping )
    {
        this.server.addFilter( filter, mapping );
    }

    protected final void addServlet( final HttpServlet servlet, final String mapping )
    {
        this.server.addServlet( servlet, mapping );
    }

    protected final Request.Builder newRequest( final String path )
    {
        return new Request.Builder().url( this.baseUrl + path );
    }

    protected final Response callRequest( final Request request )
        throws Exception
    {
        return this.client.newCall( request ).execute();
    }
}