package com.enonic.wem.web.jsp;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.base.Splitter;

import com.enonic.cms.core.product.ProductVersion;

final class JspHelperImpl
    implements JspHelper
{
    private WebApplicationContext applicationContext;

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
        final String url = ServletUriComponentsBuilder.fromRequest( this.servletRequest ).build().toString();
        final String baseUrl = url.substring( 0, url.length() - 1 );

        if ( path == null )
        {
            return baseUrl;
        }
        else
        {
            return baseUrl + "/" + path;
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

    @Override
    public <T> T getBean( final Class<T> type )
    {
        return this.applicationContext.getBean( type );
    }

    public void setServletContext( final ServletContext servletContext )
    {
        this.applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext( servletContext );
    }

    public void setServletRequest( final HttpServletRequest servletRequest )
    {
        this.servletRequest = servletRequest;
    }
}
