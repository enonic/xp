package com.enonic.wem.admin.app;

import java.net.URL;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import com.google.common.collect.Maps;
import com.samskivert.mustache.Template;

final class AppHtmlHandler
{
    private final Template template;

    public AppHtmlHandler()
    {
        final URL url = getClass().getResource( "app.html" );
        this.template = MustacheCompiler.getInstance().compile( url );
    }

    public String render( final String app, UriInfo uriInfo )
    {
        final String baseUri = uriInfo.getBaseUri().toString();

        final Map<String, Object> model = Maps.newHashMap();
        model.put( "app", app );
        model.put( "baseUri", baseUri.equals( "/" ) ? "" : baseUri );

        return this.template.execute( model );
    }
}
