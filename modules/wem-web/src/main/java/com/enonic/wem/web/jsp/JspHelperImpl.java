package com.enonic.wem.web.jsp;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import com.enonic.cms.core.product.ProductVersion;

final class JspHelperImpl
    implements JspHelper
{
    private HttpServletRequest servletRequest;

    @Override
    public String getProductVersion()
    {
        return ProductVersion.getFullTitleAndVersion();
    }

    @Override
    public String getBaseUrl()
    {
        return createUrl( null );
    }

    @Override
    public String createUrl( final String path )
    {
        StringBuilder builder =
            new StringBuilder( this.servletRequest.getScheme() ).append( "://" ).append( this.servletRequest.getServerName() );

        if ( this.servletRequest.getServerPort() != 80 )
        {
            builder.append( ":" ).append( this.servletRequest.getServerPort() );
        }
        if ( !Strings.isNullOrEmpty( this.servletRequest.getContextPath() ) )
        {
            builder.append( this.servletRequest.getContextPath() );
        }
        if ( !Strings.isNullOrEmpty( path ) )
        {
            if ( '/' != path.charAt( 0 ) )
            {
                builder.append( "/" );
            }
            builder.append( path );
        }
        return builder.toString();
    }

    @Override
    public String ellipsis( final String text, final int length )
    {
        if ( text.length() <= length )
        {
            return text;
        }
        else
        {
            final String outStr = Splitter.fixedLength( length ).split( text ).iterator().next();
            return outStr + "...";
        }
    }

    public void setServletRequest( final HttpServletRequest servletRequest )
    {
        this.servletRequest = servletRequest;
    }
}
