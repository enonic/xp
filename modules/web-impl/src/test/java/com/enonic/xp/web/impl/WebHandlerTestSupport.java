package com.enonic.xp.web.impl;

import org.junit.After;
import org.junit.Before;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import com.enonic.xp.web.handler.WebHandler;

public abstract class WebHandlerTestSupport
{
    protected JettyTestServer server;

    protected DispatcherServlet servlet;

    protected OkHttpClient client;

    protected String baseUrl;

    @Before
    public final void startServer()
        throws Exception
    {
        this.server = new JettyTestServer();
        this.server.start();

        this.servlet = new DispatcherServlet();
        this.server.addServlet( this.servlet, "/*" );

        this.client = new OkHttpClient();
        this.baseUrl = "http://localhost:" + this.server.getPort();
    }

    @After
    public final void stopServer()
        throws Exception
    {
        this.server.stop();
    }

    protected final void addHandler( final WebHandler handler )
    {
        this.servlet.addHandler( handler );
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
