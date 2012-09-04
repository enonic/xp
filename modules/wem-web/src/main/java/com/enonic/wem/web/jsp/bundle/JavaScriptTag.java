package com.enonic.wem.web.jsp.bundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

public final class JavaScriptTag
    extends TagSupport
{
    private Tag parentTag;

    private String pattern;

    @Override
    public void setParent( final Tag parent )
    {
        this.parentTag = parent;
    }

    @Override
    public int doStartTag()
        throws JspException
    {
        if (!(this.parentTag instanceof BundleTag)) {
            throw new JspException( "Parent tag must be w:bundle" );
        }

        ((BundleTag)this.parentTag).addJavaScript( this.pattern );
        return TagSupport.SKIP_BODY;
    }

    public void setPattern(final String pattern)
    {
        this.pattern = pattern;
    }
}
