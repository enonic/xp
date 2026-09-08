package com.enonic.xp.core.internal.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.owasp.html.ElementPolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.PolicyFactory;

public final class InternalHtmlSanitizer
{
    private static final String[] ALLOWED_ELEMENTS =
        new String[]{"strong", "address", "blockquote", "code", "dd", "dl", "dt", "em", "u", "p", "br", "ol", "li", "ul", "td", "th", "tr",
            "h1", "h2", "h3", "h4", "h5", "h6", "div", "pre", "strong", "a", "img", "figure", "figcaption", "table", "caption", "tbody",
            "thead", "tfoot"};

    private static final PolicyFactory STRICT_POLICY = new HtmlPolicyBuilder().allowCommonBlockElements()
        .allowCommonInlineFormattingElements()
        .allowElements( "a", "img", "pre" )
        .allowElements( "table", "caption", "thead", "tbody", "tfoot", "tr", "th", "td", "col", "colgroup" )
        .allowElements( "figure", "figcaption" )
        .allowAttributes( "href" )
        .onElements( "a" )
        .allowAttributes( "src" )
        .onElements( "img" )
        .allowAttributes( "checked", "class", "id", "target", "title", "type" )
        .globally()
        .allowAttributes( "scope" )
        .onElements( "td", "th" )
        .allowStandardUrlProtocols()
        .allowStyling()
        .toFactory();

    private static final Sanitizer STRICT_SANITIZER = new Sanitizer( html -> STRICT_POLICY, true );

    private static final Sanitizer RICH_TEXT_SANITIZER = new Sanitizer( InternalHtmlSanitizer::richTextPolicy, false );

    private InternalHtmlSanitizer()
    {
    }

    private static HtmlPolicyBuilder richTextPolicyBuilder()
    {
        return new HtmlPolicyBuilder().allowElements( ALLOWED_ELEMENTS )
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

    private static PolicyFactory richTextPolicy( final String html )
    {
        final HtmlPolicyBuilder policy = richTextPolicyBuilder();
        final Set<String> dataAttributes = dataAttributes( html );
        if ( !dataAttributes.isEmpty() )
        {
            policy.allowAttributes( dataAttributes.toArray( String[]::new ) ).globally();
        }
        return policy.toFactory();
    }

    private static Set<String> dataAttributes( final String html )
    {
        final Set<String> result = new HashSet<>();
        HtmlSanitizer.sanitize( html, new HtmlSanitizer.Policy()
        {
            @Override
            public void openDocument()
            {
            }

            @Override
            public void closeDocument()
            {
            }

            @Override
            public void openTag( final String elementName, final List<String> attrs )
            {
                for ( int i = 0; i < attrs.size(); i += 2 )
                {
                    if ( attrs.get( i ).startsWith( "data-" ) )
                    {
                        result.add( attrs.get( i ) );
                    }
                }
            }

            @Override
            public void closeTag( final String elementName )
            {
            }

            @Override
            public void text( final String text )
            {
            }
        } );
        return result;
    }

    public static final class Sanitizer
    {
        private final Function<String, PolicyFactory> policy;

        private final boolean nbspReplace;

        private Sanitizer( final Function<String, PolicyFactory> policy, final boolean nbspReplace )
        {
            this.policy = policy;
            this.nbspReplace = nbspReplace;
        }

        public String sanitize( final String value )
        {
            final String html = nbspReplace ? value.replace( "\u00A0", "&nbsp;" ) : value;
            return policy.apply( html ).sanitize( html );
        }
    }

    public static Sanitizer richText()
    {
        return RICH_TEXT_SANITIZER;
    }

    public static Sanitizer strict()
    {
        return STRICT_SANITIZER;
    }

    private static ElementPolicy getImgElementPolicy()
    {
        return ( elementName, attrs ) -> {
            if ( "img".equals( elementName ) && attrs.contains( "src" ) )
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

                attrs.clear( );
                attrs.addAll( toRemain );

                return elementName;
            }
            return null;
        };
    }
}
