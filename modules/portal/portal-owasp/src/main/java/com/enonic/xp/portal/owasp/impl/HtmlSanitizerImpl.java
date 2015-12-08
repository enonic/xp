package com.enonic.xp.portal.owasp.impl;

import org.osgi.service.component.annotations.Component;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import com.enonic.xp.portal.owasp.HtmlSanitizer;

@Component(immediate = true)
public final class HtmlSanitizerImpl
    implements HtmlSanitizer
{

    private static final PolicyFactory HTML_SANITIZE_POLICY = new HtmlPolicyBuilder().
        allowCommonBlockElements().
        allowCommonInlineFormattingElements().
        allowElements( "a", "img" ).
        allowAttributes( "href" ).onElements( "a" ).
        allowAttributes( "src" ).onElements( "img" ).
        allowAttributes( "checked", "class", "id", "target", "title", "type" ).globally().
        allowStandardUrlProtocols().
        allowStyling().
        toFactory();

    @Override
    public String sanitizeHtml( final String html )
    {
        return HTML_SANITIZE_POLICY.sanitize( html ).replace( "\u00A0", "&nbsp;" );
    }
}
