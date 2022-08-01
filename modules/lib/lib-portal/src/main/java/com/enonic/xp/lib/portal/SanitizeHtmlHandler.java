package com.enonic.xp.lib.portal;

import com.enonic.xp.portal.owasp.HtmlSanitizer;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class SanitizeHtmlHandler
    implements ScriptBean
{
    private HtmlSanitizer htmlSanitizer;

    public String sanitizeHtml( final String html )
    {
        return html == null ? null : htmlSanitizer.sanitizeHtml( html );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.htmlSanitizer = context.getService( HtmlSanitizer.class, "(type=function)" ).get();
    }
}
