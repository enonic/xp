package com.enonic.wem.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

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
        final StringBuilder str = new StringBuilder();
        str.append( req.getScheme() ).append( "://" ).append( req.getServerName() );

        if ( req.getServerPort() != 80 )
        {
            str.append( ":" ).append( req.getServerPort() );
        }

        str.append( req.getContextPath() );
        if ( str.charAt( str.length() - 1 ) == '/' )
        {
            str.deleteCharAt( str.length() - 1 );
        }

        return str.toString();
    }
}
