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
        String url = ServletUriComponentsBuilder.fromRequest( this.servletRequest ).build().toString();
        if ( url.endsWith( "/" ) )
        {
            url = url.substring( 0, url.length() - 1 );
        }

        if ( Strings.isNullOrEmpty( path ) )
        {
            return url;
        }
        else
        {
            return url + "/" + path;
        }
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
