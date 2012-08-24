package com.enonic.wem.web.jsp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromContextPath( this.servletRequest );
        if ( !Strings.isNullOrEmpty( path ) )
        {
            if ( '/' == path.charAt( 0 ) )
            {
                builder.pathSegment( path.substring( 1 ) );
            }
            else
            {
                builder.pathSegment( path );
            }
        }
        return builder.build().toString();
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
