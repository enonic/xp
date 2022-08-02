package com.enonic.xp.portal.owasp.impl;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

final class HtmlStrictSanitizer
{
    private final PolicyFactory htmlSanitizePolicy;

    HtmlStrictSanitizer()
    {
        this.htmlSanitizePolicy = new HtmlPolicyBuilder().allowCommonBlockElements()
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
    }

    String sanitize( final String html )
    {
        return htmlSanitizePolicy.sanitize( html ).replace( "\u00A0", "&nbsp;" );
    }
}
