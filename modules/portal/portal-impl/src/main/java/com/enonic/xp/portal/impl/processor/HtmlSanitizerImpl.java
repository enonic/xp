package com.enonic.xp.portal.impl.processor;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.core.internal.processor.InternalHtmlSanitizer;
import com.enonic.xp.portal.owasp.HtmlSanitizer;

@Component(immediate = true)
public final class HtmlSanitizerImpl
    implements HtmlSanitizer
{
    @Override
    public String sanitizeHtml( final String html )
    {
        return InternalHtmlSanitizer.strict().sanitize( html );
    }

}
