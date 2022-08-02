package com.enonic.xp.portal.owasp;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface HtmlSanitizer
{
    String sanitizeHtml( String html );

    String sanitizeHtml( String html, SanitizeType type );
}
