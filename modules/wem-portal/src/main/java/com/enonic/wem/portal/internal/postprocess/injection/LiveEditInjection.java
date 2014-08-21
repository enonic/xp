package com.enonic.wem.portal.internal.postprocess.injection;

import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import javax.inject.Singleton;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.internal.controller.JsContext;

@Singleton
public final class LiveEditInjection
    implements PostProcessInjection
{
    private final Template headEndTemplate;

    private final Template bodyEndTemplate;

    public LiveEditInjection()
    {
        this.headEndTemplate = compileTemplate( "liveEditHeadEnd.html" );
        this.bodyEndTemplate = compileTemplate( "liveEditBodyEnd.html" );
    }

    @Override
    public String inject( final JsContext context, final Tag tag )
    {
        if ( RenderingMode.EDIT != context.getRequest().getMode() )
        {
            return null;
        }

        if ( tag == Tag.HEAD_END )
        {
            return injectHeadEnd( context );
        }

        if ( tag == Tag.BODY_END )
        {
            return injectBodyEnd( context );
        }

        return null;
    }

    private String injectHeadEnd( final JsContext context )
    {
        return injectUsingTemplate( context, this.headEndTemplate );
    }

    private String injectBodyEnd( final JsContext context )
    {
        return injectUsingTemplate( context, this.bodyEndTemplate );
    }

    private String injectUsingTemplate( final JsContext context, final Template template )
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( "adminUrl", context.getUrl().getBaseUrl() + "/admin" );

        final StringWriter out = new StringWriter();
        template.execute( map, out );
        out.write( '\n' );
        return out.toString();
    }

    private Template compileTemplate( final String name )
    {
        try
        {
            final URL url = getClass().getResource( name );
            final String str = Resources.toString( url, Charsets.UTF_8 );
            return Mustache.compiler().compile( str.trim() );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( "Failed to compile template [" + name + "]", e );
        }
    }
}
