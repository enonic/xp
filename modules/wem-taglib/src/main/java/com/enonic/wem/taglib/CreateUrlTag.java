package com.enonic.wem.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public final class CreateUrlTag
    extends TagSupport
{
    private String var;

    private String path;

    public void setVar( final String var )
    {
        this.var = var;
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    @Override
    public int doStartTag()
        throws JspException
    {
        final String url = createUrl();
        this.pageContext.setAttribute( this.var, url );
        return SKIP_BODY;
    }

    private String createUrl()
    {
        final HttpServletRequest req = (HttpServletRequest)this.pageContext.getRequest();
        final String baseUrl = getBaseUrl( req );

        if (this.path == null) {
            return baseUrl;
        } else {
            return baseUrl + "/" + this.path;
        }
    }

    private static String getBaseUrl( final HttpServletRequest req )
    {
        final String url = ServletUriComponentsBuilder.fromRequest( req ).build().toString();
        return url.substring( 0, url.length() - 1 );
    }
}
