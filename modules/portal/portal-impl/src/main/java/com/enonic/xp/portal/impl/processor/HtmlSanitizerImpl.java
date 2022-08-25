package com.enonic.xp.portal.impl.processor;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.core.internal.processor.InternalHtmlSanitizer;
import com.enonic.xp.portal.owasp.HtmlSanitizer;
import com.enonic.xp.portal.owasp.SanitizeType;

@Component(immediate = true)
public final class HtmlSanitizerImpl
    implements HtmlSanitizer
{
    public HtmlSanitizerImpl()
    {
    }

    @Override
    public String sanitizeHtml( final String html )
    {
        return InternalHtmlSanitizer.strict().sanitize( html );
    }

    @Override
    public String sanitizeHtml( final String html, final SanitizeType type )
    {
        switch ( type )
        {
            case STRICT:
                return InternalHtmlSanitizer.strict().sanitize( html );
            case RICH_TEXT:
                return InternalHtmlSanitizer.richText().sanitize( html );
            default:
                return html;
        }
    }
}
