package com.enonic.wem.portal.internal.url;


import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.portal.url.ServiceUrlBuilder;

public final class ServiceUrlBuilderImpl
    implements ServiceUrlBuilder
{
    private final UrlBuilder urlBuilder;

    private String serviceName;

    public ServiceUrlBuilderImpl( final String baseUrl )
    {
        this.serviceName = "";
        this.urlBuilder = new UrlBuilder( baseUrl );
        this.urlBuilder.beforeBuildUrl( ( urlBuilder ) -> {
            urlBuilder.resourceType( UrlBuilder.SERVICE_RESOURCE );
            urlBuilder.resourcePath( this.serviceName );
        } );
    }

    @Override
    public ServiceUrlBuilder mode( final String mode )
    {
        this.urlBuilder.mode( mode );
        return this;
    }

    @Override
    public ServiceUrlBuilder module( final String module )
    {
        this.urlBuilder.module( module );
        return this;
    }

    @Override
    public ServiceUrlBuilder param( final String name, final Object value )
    {
        this.urlBuilder.param( name, value );
        return this;
    }

    @Override
    public ServiceUrlBuilder contentPath( final String contentPath )
    {
        this.urlBuilder.contentPath( contentPath );
        return this;
    }

    @Override
    public ServiceUrlBuilder contentPath( final ContentPath contentPath )
    {
        this.urlBuilder.contentPath( contentPath );
        return this;
    }

    @Override
    public ServiceUrlBuilder serviceName( final String name )
    {
        this.serviceName = name;
        return this;
    }

    @Override
    public String toString()
    {
        return this.urlBuilder.toString();
    }
}