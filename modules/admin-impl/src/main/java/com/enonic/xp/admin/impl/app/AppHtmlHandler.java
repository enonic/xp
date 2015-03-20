package com.enonic.xp.admin.impl.app;

import java.net.URL;
import java.util.Map;

import com.google.common.collect.Maps;
import com.samskivert.mustache.Template;

import com.enonic.xp.Version;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

final class AppHtmlHandler
{
    private final Template template;

    private final String version;

    public AppHtmlHandler()
    {
        final URL url = getClass().getResource( "app.html" );
        this.template = MustacheCompiler.getInstance().compile( url );

        final String version = Version.get().getVersion();
        if ( version.endsWith( "-SNAPSHOT" ) )
        {
            this.version = Long.toString( System.currentTimeMillis() );
        }
        else
        {
            this.version = version;
        }
    }

    public String render( final String app )
    {
        final String baseUri = ServletRequestUrlHelper.createUri( "" );

        final Map<String, Object> model = Maps.newHashMap();
        model.put( "app", app );

        final String uri = baseUri.equals( "/" ) ? "" : baseUri;
        model.put( "baseUri", uri );
        model.put( "assetsUri", uri + "/admin/assets/" + this.version );
        model.put( "xpVersion", this.version );

        return this.template.execute( model );
    }
}
