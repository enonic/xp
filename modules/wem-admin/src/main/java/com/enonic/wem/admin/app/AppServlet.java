package com.enonic.wem.admin.app;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;

import com.enonic.wem.core.mustache.MustacheService;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

@Singleton
public final class AppServlet
    extends HttpServlet
{
    private final static String DEFAULT_APP_NAME = "app-launcher";

    @Inject
    protected MustacheService mustacheService;

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

        final Map<String, Object> model = Maps.newHashMap();
        model.put( "app", app != null ? app : DEFAULT_APP_NAME );
        model.put( "baseUrl", baseUrl );

        resp.setContentType( "text/html" );
        resp.setCharacterEncoding( "UTF-8" );

        final URL url = getClass().getResource( "app.html" );
        final Reader reader = new InputStreamReader( url.openStream() );
        final String result = this.mustacheService.render( reader, model );

        final Writer writer = resp.getWriter();
        writer.write( result );
        writer.flush();
    }
}
