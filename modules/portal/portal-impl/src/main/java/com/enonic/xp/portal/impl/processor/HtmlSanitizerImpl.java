package com.enonic.xp.portal.impl.processor;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.core.internal.processor.HtmlRichTextSanitizer;
import com.enonic.xp.core.internal.processor.HtmlStrictSanitizer;
import com.enonic.xp.portal.owasp.HtmlSanitizer;
import com.enonic.xp.portal.owasp.SanitizeType;

@Component(immediate = true)
public final class HtmlSanitizerImpl
    implements HtmlSanitizer
{
    private final HtmlStrictSanitizer htmlStrictSanitizer;

    private final HtmlRichTextSanitizer htmlRichTextSanitizer;

    public HtmlSanitizerImpl()
    {
        this.htmlStrictSanitizer = new HtmlStrictSanitizer();
        this.htmlRichTextSanitizer = new HtmlRichTextSanitizer();
    }

    @Override
    public String sanitizeHtml( final String html )
    {
        return htmlStrictSanitizer.sanitize( html );
    }

    @Override
    public String sanitizeHtml( final String html, final SanitizeType type )
    {
        switch ( type )
        {
            case STRICT:
                return htmlStrictSanitizer.sanitize( html );
            case RICH_TEXT:
                return htmlRichTextSanitizer.sanitize( html );
            default:
                return html;
        }
    }
}
