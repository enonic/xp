package com.enonic.wem.web.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

public final class JspHelperTag
    extends TagSupport
{
    private String var;

    public void setVar( final String var )
    {
        this.var = var;
    }

    @Override
    public int doStartTag()
        throws JspException
    {
        final JspHelperImpl helper = new JspHelperImpl();
        helper.setServletContext( this.pageContext.getServletContext() );
        helper.setServletRequest( (HttpServletRequest) this.pageContext.getRequest() );

        this.pageContext.setAttribute( this.var, helper );
        return Tag.SKIP_BODY;
    }
}
