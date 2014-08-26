package com.enonic.wem.portal.internal.url;


import java.util.Map;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.portal.url.GeneralUrlBuilder;

public final class GeneralUrlBuilderImpl
    implements GeneralUrlBuilder
{
    private final UrlBuilder urlBuilder;

    public GeneralUrlBuilderImpl( final String baseUrl )
    {
        this.urlBuilder = new UrlBuilder( baseUrl );
    }

    @Override
    public GeneralUrlBuilder mode( final String mode )
    {
        this.urlBuilder.mode( mode );
        return this;
    }

    @Override
    public GeneralUrlBuilder workspace( final String workspace )
    {
        this.urlBuilder.workspace( workspace );
        return this;
    }

    @Override
    public GeneralUrlBuilder resourcePath( final String path )
    {
        this.urlBuilder.resourcePath( path );
        return this;
    }

    @Override
    public GeneralUrlBuilder resourceType( final String resourceType )
    {
        this.urlBuilder.resourceType( resourceType );
        return this;
    }

    @Override
    public GeneralUrlBuilder params( final Map<String, Object> params )
    {
        this.urlBuilder.params( params );
        return this;
    }

    @Override
    public GeneralUrlBuilder param( final String name, final Object value )
    {
        this.urlBuilder.param( name, value );
        return this;
    }

    @Override
    public GeneralUrlBuilder contentPath( final String contentPath )
    {
        this.urlBuilder.contentPath( contentPath );
        return this;
    }

    @Override
    public GeneralUrlBuilder contentPath( final ContentPath contentPath )
    {
        this.urlBuilder.contentPath( contentPath );
        return this;
    }

    @Override
    public GeneralUrlBuilder module( final String module )
    {
        this.urlBuilder.module( module );
        return this;
    }

    @Override
    public String toString()
    {
        return this.urlBuilder.toString();
    }
}