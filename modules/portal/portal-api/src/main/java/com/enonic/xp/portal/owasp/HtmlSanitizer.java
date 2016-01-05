package com.enonic.xp.portal.owasp;

import com.google.common.annotations.Beta;

@Beta
public interface HtmlSanitizer
{
    String sanitizeHtml( String html );
}
