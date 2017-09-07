package com.enonic.xp.app.contentstudio;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
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
    private LocaleService localeService;

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
        return injectUsingTemplate( this.headEndTemplate, makeModelForHeadEnd( portalRequest ) );
    }

    private String injectBodyEnd( final PortalRequest portalRequest )
    {
        return injectUsingTemplate( this.bodyEndTemplate, makeModelForBodyEnd( portalRequest ) );
    }

    private String injectUsingTemplate( final StringTemplate template, final Map<String, String> model )
    {
        return template.apply( model ).trim() + "\n";
    }

    private Map<String, String> makeModelForHeadEnd( final PortalRequest portalRequest )
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( "assetsUrl", portalRequest.rewriteUri( "/admin/_/asset/com.enonic.xp.app.contentstudio" ) );
        return map;
    }

    private Map<String, String> makeModelForBodyEnd( final PortalRequest portalRequest )
    {
        final Map<String, String> map = makeModelForHeadEnd( portalRequest );

        final MessageBundle bundle =
            this.localeService.getBundle( ApplicationKey.from( "com.enonic.xp.app.contentstudio" ), resolveLocale( portalRequest ),
                                          "admin/i18n/common", "admin/i18n/phrases" );

        map.put( "messages", convertBundleToString( bundle ) );

        return map;
    }

    private Locale resolveLocale( final PortalRequest portalRequest )
    {
        return portalRequest.getRawRequest().getLocale();
    }

    private String convertBundleToString( final MessageBundle bundle )
    {
        StringBuilder sb = new StringBuilder( "{" );
        Iterator<Map.Entry<String, String>> bundleIterator = bundle.asMap().entrySet().iterator();
        while ( bundleIterator.hasNext() )
        {
            Map.Entry<String, String> entry = bundleIterator.next();
            sb.append( '"' ).append( entry.getKey() ).append( '"' );
            sb.append( ':' );
            sb.append( '"' ).append( entry.getValue().replace( "\"", "\\\"" ) ).append( '"' );
            if ( bundleIterator.hasNext() )
            {
                sb.append( ',' );
            }
        }
        sb.append( "}" );

        return sb.toString();
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
