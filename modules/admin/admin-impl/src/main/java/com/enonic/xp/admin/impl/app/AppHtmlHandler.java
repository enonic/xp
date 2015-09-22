package com.enonic.xp.admin.impl.app;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.util.StringTemplate;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

final class AppHtmlHandler
{
    private final StringTemplate template;

    private final String version;

    public AppHtmlHandler()
    {
        this.template = StringTemplate.load( getClass(), "app.html" );

        final VersionInfo version = VersionInfo.get();
        if ( version.isSnapshot() )
        {
            this.version = Long.toString( System.currentTimeMillis() );
        }
        else
        {
            this.version = version.getVersion();
        }
    }

    public String render( final String app )
    {
        final String baseUri = ServletRequestUrlHelper.createUri( "" );

        final Map<String, String> model = Maps.newHashMap();
        model.put( "app", app );

        final String uri = baseUri.equals( "/" ) ? "" : baseUri;
        model.put( "baseUri", uri );
        model.put( "assetsUri", uri + "/admin/assets/" + this.version );
        model.put( "xpVersion", VersionInfo.get().getVersion() );

        return this.template.apply( model );
    }
}
