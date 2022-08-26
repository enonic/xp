package com.enonic.xp.core.internal.processor;

import java.util.ArrayList;
import java.util.List;

import org.owasp.html.ElementPolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlStreamEventProcessor;
import org.owasp.html.HtmlStreamEventReceiver;
import org.owasp.html.HtmlStreamEventReceiverWrapper;

public final class InternalHtmlSanitizer
{
    private static final String[] ALLOWED_ELEMENTS =
        new String[]{"strong", "address", "blockquote", "code", "dd", "dl", "dt", "em", "u", "p", "br", "ol", "li", "ul", "td", "th", "tr",
            "h1", "h2", "h3", "h4", "h5", "h6", "div", "pre", "strong", "a", "img", "figure", "figcaption", "table", "caption", "tbody",
            "thead", "tfoot"};

    private static final HtmlPolicyBuilder RICH_TEXT_POLICY = new HtmlPolicyBuilder().allowElements( ALLOWED_ELEMENTS )
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
        .allowAttributes( "accesskey", "charset", "dir", "download", "href", "id", "lang", "name", "onclick", "rel", "tabindex", "target",
                          "title", "type" )
        .onElements( "a" )
        .allowAttributes( "align", "border", "cellpadding", "cellspacing", "summary" )
        .onElements( "table" )
        .allowAttributes( "scope" )
        .onElements( "td", "th", "tr" )
        .allowStandardUrlProtocols()
        .allowUrlProtocols( "content", "media", "image" )
        .allowStyling();

    private static final HtmlPolicyBuilder STRICT_POLICY = new HtmlPolicyBuilder().allowCommonBlockElements()
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
        .allowStyling();

    private static final Sanitizer STRICT_SANITIZER = new Sanitizer( STRICT_POLICY, true );

    private static final Sanitizer RICH_TEXT_SANITIZER =
        new Sanitizer( RICH_TEXT_POLICY, ( HtmlStreamEventReceiver r ) -> new HtmlStreamEventReceiverWrapper( r )
        {
            @Override
            public void openTag( String elementName, List<String> attrs )
            {
                attrs.stream()
                    .filter( attr -> attr.startsWith( "data-" ) )
                    .forEach( attr -> RICH_TEXT_POLICY.allowAttributes( attr ).globally() );

                super.openTag( elementName, attrs );
            }
        }, false );

    private InternalHtmlSanitizer()
    {
    }

    public static final class Sanitizer
    {
        private final HtmlPolicyBuilder policy;

        private final HtmlStreamEventProcessor processor;

        private final boolean nbspReplace;

        private Sanitizer( final HtmlPolicyBuilder policy, final boolean nbspReplace )
        {
            this.policy = policy;
            this.processor = null;
            this.nbspReplace = nbspReplace;
        }

        private Sanitizer( final HtmlPolicyBuilder policy, HtmlStreamEventProcessor processor, final boolean nbspReplace )
        {
            this.policy = policy;
            this.processor = processor;
            this.nbspReplace = nbspReplace;
        }

        public String sanitize( final String value )
        {
            if ( processor != null )
            {
                policy.withPreprocessor( processor ).toFactory().sanitize( value );
            }

            return policy.toFactory().sanitize( nbspReplace ? value.replace( "\u00A0", "&nbsp;" ) : value );
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

                attrs.removeAll( attrs );
                attrs.addAll( toRemain );

                return elementName;
            }
            return null;
        };
    }
}
