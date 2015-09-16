package com.enonic.xp.portal.impl.postprocess.injection;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Maps;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.postprocess.PostProcessInjection;
import com.enonic.xp.util.StringTemplate;

@Component(immediate = true, service = PostProcessInjection.class)
public final class LiveEditInjection
    implements PostProcessInjection
{
    private final StringTemplate headEndTemplate;

    private final StringTemplate bodyEndTemplate;

    public LiveEditInjection()
    {
        this.headEndTemplate = StringTemplate.load( getClass(), "liveEditHeadEnd.html" );
        this.bodyEndTemplate = StringTemplate.load( getClass(), "liveEditBodyEnd.html" );
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
            return Collections.singletonList( injectHeadEnd( portalRequest ) );
        }

        if ( htmlTag == HtmlTag.BODY_END )
        {
            return Collections.singletonList( injectBodyEnd( portalRequest ) );
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

    private String injectUsingTemplate( final PortalRequest portalRequest, final StringTemplate template )
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( "adminUrl", portalRequest.rewriteUri( "/admin" ) );
        return template.apply( map ).trim() + "\n";
    }
}
