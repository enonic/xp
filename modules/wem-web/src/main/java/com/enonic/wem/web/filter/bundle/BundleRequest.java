package com.enonic.wem.web.filter.bundle;

import java.net.URL;

import javax.servlet.ServletContext;

public final class BundleRequest
{
    private String requestPath;

    private ServletContext servletContext;

    private String cacheTimestamp;

    private URL bundleJsonUrl;

    private BundleModel bundleModel;

    public String getRequestPath()
    {
        return requestPath;
    }

    public void setRequestPath( final String requestPath )
    {
        this.requestPath = requestPath;
    }

    public ServletContext getServletContext()
    {
        return servletContext;
    }

    public void setServletContext( final ServletContext servletContext )
    {
        this.servletContext = servletContext;
    }

    public URL getBundleJsonUrl()
    {
        return bundleJsonUrl;
    }

    public void setBundleJsonUrl( final URL bundleJsonUrl )
    {
        this.bundleJsonUrl = bundleJsonUrl;
    }

    public String getBasePath()
    {
        return this.requestPath.substring( 0, requestPath.lastIndexOf( '/' ) );
    }

    public String getCacheTimestamp()
    {
        return cacheTimestamp;
    }

    public void setCacheTimestamp( final String cacheTimestamp )
    {
        this.cacheTimestamp = cacheTimestamp;
    }

    public String getCacheKey()
    {
        if (this.cacheTimestamp != null) {
            return this.requestPath + "_" + this.cacheTimestamp;
        } else {
            return null;
        }
    }

    public BundleModel getBundleModel()
    {
        return bundleModel;
    }

    public void setBundleModel( final BundleModel bundleModel )
    {
        this.bundleModel = bundleModel;
    }

    public boolean shouldCompress()
    {
        return this.cacheTimestamp != null;
    }

    public boolean isDebugMode()
    {
        return this.cacheTimestamp == null;
    }
}
