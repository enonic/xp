package com.enonic.wem.admin.app;

import java.io.IOException;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import com.enonic.wem.core.servlet.ServletRequestUrlHelper;

@Singleton
public final class AppServlet
    extends HttpServlet
{
    private final static String DEFAULT_APP_NAME = "app-launcher";

    private Configuration config;

    @Override
    public void init()
        throws ServletException
    {
        this.config = new Configuration();
        this.config.setServletContextForTemplateLoading( getServletContext(), "/WEB-INF/views" );
    }

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse resp )
        throws ServletException, IOException
    {
        final String app = req.getParameter( "app" );
        render( app, resp );
    }

    private void render( final String app, final HttpServletResponse resp )
        throws IOException
    {
        final String baseUrl = ServletRequestUrlHelper.createUrl( "" );

        final Map<String, String> model = Maps.newHashMap();
        model.put( "app", app != null ? app : DEFAULT_APP_NAME );
        model.put( "baseUrl", baseUrl );

        try
        {
            resp.setContentType( "text/html" );
            resp.setCharacterEncoding( "UTF-8" );

            final Template template = this.config.getTemplate( "app.ftl" );
            template.process( model, resp.getWriter() );
        }
        catch ( final TemplateException e )
        {
            handleError( resp, e );
        }
    }

    private void handleError( final HttpServletResponse resp, final TemplateException e )
        throws IOException
    {
        resp.setContentType( "text/plain" );
        resp.setCharacterEncoding( "UTF-8" );
        e.printStackTrace( resp.getWriter() );
    }
}
