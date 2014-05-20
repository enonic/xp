package com.enonic.wem.admin.app;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;
import com.samskivert.mustache.Template;

import com.enonic.wem.core.mustache.MustacheCompiler;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

final class AppHtmlHandler
{
    private final static String DEFAULT_APP_NAME = "app-launcher";

    private final Template template;

    public AppHtmlHandler()
    {
        final URL url = getClass().getResource( "app.html" );
        this.template = MustacheCompiler.getInstance().compile( url );
    }

    public void handle( final HttpServletRequest req, final HttpServletResponse resp )
        throws ServletException, IOException
    {
        final String app = req.getParameter( "app" );
        render( app, req, resp );
    }

    private void render( final String app, final HttpServletRequest req, final HttpServletResponse resp )
        throws IOException
    {
        final String baseUrl = ServletRequestUrlHelper.createUrl( req, "" );

        final Map<String, Object> model = Maps.newHashMap();
        model.put( "app", app != null ? app : DEFAULT_APP_NAME );
        model.put( "baseUrl", baseUrl );

        resp.setContentType( "text/html" );
        resp.setCharacterEncoding( "UTF-8" );

        this.template.execute( model, resp.getWriter() );
    }
}
