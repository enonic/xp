package com.enonic.xp.admin.impl.app;

import java.net.URL;
import java.util.Map;

import com.google.common.collect.Maps;
import com.samskivert.mustache.Template;

import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

final class AppHtmlHandler
{
    private final Template template;

    public AppHtmlHandler()
    {
        final URL url = getClass().getResource( "app.html" );
        this.template = MustacheCompiler.getInstance().compile( url );
    }

    public String render( final String app )
    {
        final String baseUri = ServletRequestUrlHelper.createUri( "" );

        final Map<String, Object> model = Maps.newHashMap();
        model.put( "app", app );
        model.put( "baseUri", baseUri.equals( "/" ) ? "" : baseUri );

        return this.template.execute( model );
    }
}
