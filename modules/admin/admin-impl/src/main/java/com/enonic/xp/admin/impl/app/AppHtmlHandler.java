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
        final String assetsUri = uri + "/admin/assets/" + this.version;
        model.put( "assetsUri", assetsUri );
        model.put( "xpVersion", VersionInfo.get().getVersion() );
        model.put( "customBodyPart", renderCustomBodyPart( app, assetsUri ) );

        return this.template.apply( model );
    }

    private String renderCustomBodyPart( final String app, final String assetsUri )
    {
        if ( isSystemApplication( app ) )
        {
            return renderSystemCustomBodyPart( app, assetsUri );
        }
        return renderApplicationCustomBodyPart( app, assetsUri );
    }

    private String renderSystemCustomBodyPart( final String adminApplicationKey, final String assetsUri )
    {
        final Map<String, String> model = Maps.newHashMap();
        model.put( "app", adminApplicationKey );
        model.put( "assetsUri", assetsUri );

        return StringTemplate.load( getClass(), "customBodyPart-system.html" ).
            apply( model );
    }

    private String renderApplicationCustomBodyPart( final String adminApplicationKey, final String assetsUri )
    {
        final Map<String, String> model = Maps.newHashMap();
        final String[] adminApplicationKeyValues = adminApplicationKey.split( ":" );
        model.put( "application", adminApplicationKeyValues[0] );
        model.put( "adminApplication", adminApplicationKeyValues[1] );
        model.put( "assetsUri", assetsUri );

        return StringTemplate.load( getClass(), "customBodyPart-application.html" ).
            apply( model );
    }

    private boolean isSystemApplication( final String app )
    {
        return !app.contains( ":" );
    }
}
