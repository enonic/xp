package com.enonic.xp.portal.owasp.impl;

import java.util.ArrayList;
import java.util.List;

import org.owasp.html.ElementPolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlStreamEventReceiver;
import org.owasp.html.HtmlStreamEventReceiverWrapper;

final class HtmlRichTextSanitizer
{
    private static final String[] ALLOWED_ELEMENTS =
        new String[]{"strong", "address", "blockquote", "code", "dd", "dl", "dt", "em", "u", "p", "br", "ol", "li", "ul", "td", "th", "tr",
            "h1", "h2", "h3", "h4", "h5", "h6", "div", "pre", "strong", "a", "img", "figure", "figcaption", "table", "caption", "tbody",
            "thead", "tfoot"};

    private final HtmlPolicyBuilder htmlSanitizePolicy;

    HtmlRichTextSanitizer()
    {
        final HtmlPolicyBuilder htmlPolicyBuilder = new HtmlPolicyBuilder();

        this.htmlSanitizePolicy = htmlPolicyBuilder.allowElements( ALLOWED_ELEMENTS )
            .allowElements( getImgElementPolicy(), "img" )
            .allowAttributes( "class" )
            .globally()//classes to everyone
            .allowAttributes( "start" )
            .onElements( "ol" )
            .allowAttributes( "value" )
            .onElements( "li" )
            .allowAttributes( "colspan", "rowspan" )
            .onElements( "td", "th" )
            .allowAttributes( "alt", "width", "height", "src" )
            .onElements( "img" )
            .allowAttributes( "accesskey", "charset", "dir", "download", "href", "id", "lang", "name", "onclick", "rel", "tabindex",
                              "target", "title", "type" )
            .onElements( "a" )
            .allowAttributes( "align", "border", "cellpadding", "cellspacing", "summary" )
            .onElements( "table" )
            .allowAttributes( "scope" )
            .onElements( "td", "th", "tr" )
            .allowStandardUrlProtocols()
            .allowUrlProtocols( "content", "media", "image" )
            .allowStyling();
    }

    String sanitize( final String html )
    {
        htmlSanitizePolicy.withPreprocessor( ( HtmlStreamEventReceiver r ) -> {
            return new HtmlStreamEventReceiverWrapper( r )
            {
                @Override
                public void openTag( String elementName, List<String> attrs )
                {
                    attrs.stream()
                        .filter( attr -> attr.startsWith( "data-" ) )
                        .forEach( attr -> htmlSanitizePolicy.allowAttributes( attr ).globally() );

                    super.openTag( elementName, attrs );
                }
            };
        } ).toFactory().sanitize( html );

        return htmlSanitizePolicy.toFactory().sanitize( html.replace( "\u00A0", "&nbsp;" ) );
    }

    private ElementPolicy getImgElementPolicy()
    {
        return ( elementName, attrs ) -> {
            if ( "img".equals( elementName ) )
            {
                if ( attrs.contains( "src" ) )
                {
                    final List<String> toRemain = new ArrayList<>();
                    final List<String> allowedAttrs = List.of( "alt", "src" );

                    for ( int i = 0; i < attrs.size(); i += 2 )
                    {
                        if ( allowedAttrs.contains( attrs.get( i ) ) || attrs.get( i ).startsWith( "data-" ) )
                        {
                            toRemain.add( attrs.get( i ) );
                            toRemain.add( attrs.get( i + 1 ) );
                        }
                    }

                    attrs.removeAll( attrs );
                    attrs.addAll( toRemain );

                    return elementName;

                }
            }
            return null;
        };
    }
}
