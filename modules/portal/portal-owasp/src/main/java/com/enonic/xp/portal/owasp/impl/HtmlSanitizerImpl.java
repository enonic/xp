package com.enonic.xp.portal.owasp.impl;

import org.osgi.service.component.annotations.Component;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import com.enonic.xp.portal.owasp.HtmlSanitizer;

@Component(immediate = true, property = "type=function")
public final class HtmlSanitizerImpl
    implements HtmlSanitizer
{

    private final PolicyFactory htmlSanitizePolicy;

    public HtmlSanitizerImpl()
    {
        this.htmlSanitizePolicy = new HtmlPolicyBuilder().
            allowCommonBlockElements().
            allowCommonInlineFormattingElements().
            allowElements( "a", "img", "pre" ).
            allowElements( "table", "caption", "thead", "tbody", "tfoot", "tr", "th", "td", "col", "colgroup" ).
            allowElements( "figure", "figcaption" ).
            allowAttributes( "href" ).onElements( "a" ).
            allowAttributes( "src" ).onElements( "img" ).
            allowAttributes( "checked", "class", "id", "target", "title", "type" ).globally().
            allowAttributes( "scope" ).onElements( "td", "th" ).
            allowStandardUrlProtocols().
            allowStyling().
            toFactory();
    }

    @Override
    public String sanitizeHtml( final String html )
    {
        return htmlSanitizePolicy.sanitize( html ).replace( "\u00A0", "&nbsp;" );
    }
}
