package com.enonic.xp.portal.impl.postprocess.injection;

import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.postprocess.PostProcessInjection;

@Component(immediate = true)
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
    public List<String> inject( final PortalRequest portalRequest, final PortalResponse portalResponse, final HtmlTag htmlTag )
    {
        if ( RenderMode.EDIT != portalRequest.getMode() )
        {
            return null;
        }

        if ( htmlTag == HtmlTag.HEAD_END )
        {
            return Arrays.asList( injectHeadEnd( portalRequest ) );
        }

        if ( htmlTag == HtmlTag.BODY_END )
        {
            return Arrays.asList( injectBodyEnd( portalRequest ) );
        }

        return null;
    }

    private String injectHeadEnd( final PortalRequest portalRequest )
    {
        return injectUsingTemplate( portalRequest, this.headEndTemplate );
    }

    private String injectBodyEnd( final PortalRequest portalRequest )
    {
        return injectUsingTemplate( portalRequest, this.bodyEndTemplate );
    }

    private String injectUsingTemplate( final PortalRequest portalRequest, final Template template )
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( "adminUrl", portalRequest.rewriteUri( "/admin" ) );

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
