package com.enonic.wem.portal.internal.url;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.portal.url.ImageUrlBuilder;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;

public final class ImageUrlBuilderImpl
    implements ImageUrlBuilder
{
    private final UrlBuilder urlBuilder;

    private static final String IMAGE_RESOURCE_TYPE = "image";

    private String background;

    private Integer quality;

    private List<String> filters;

    public ImageUrlBuilderImpl( final String baseUrl )
    {
        background = "";
        quality = null;
        this.filters = Lists.newArrayList();
        this.urlBuilder = new UrlBuilder( baseUrl );
        this.urlBuilder.beforeBuildUrl( ( urlBuilder ) -> {
            urlBuilder.resourceType( IMAGE_RESOURCE_TYPE );

            if ( !filters.isEmpty() )
            {
                final String filtersStr = Joiner.on( ";" ).skipNulls().join( filters );
                urlBuilder.param( "filter", filtersStr );
            }
            if ( !background.isEmpty() )
            {
                urlBuilder.param( "background", background );
            }
            if ( quality != null )
            {
                urlBuilder.param( "quality", quality );
            }
        } );
    }

    @Override
    public ImageUrlBuilder filter( final String... filters )
    {
        Collections.addAll( this.filters, filters );
        return this;
    }

    @Override
    public ImageUrlBuilder background( final String backgroundColor )
    {
        this.background = nullToEmpty( backgroundColor );
        return this;
    }

    @Override
    public ImageUrlBuilder quality( final int quality )
    {
        checkArgument( quality > 0, "Image Quality must be between 1 and 100. Value: %s", quality );
        checkArgument( quality <= 100, "Image Quality must be between 1 and 100. Value: %s", quality );
        this.quality = quality;
        return this;
    }

    @Override
    public ImageUrlBuilder mode( final String mode )
    {
        this.urlBuilder.mode( mode );
        return this;
    }

    @Override
    public ImageUrlBuilder workspace( final String workspace )
    {
        this.urlBuilder.workspace( workspace );
        return this;
    }

    @Override
    public ImageUrlBuilder resourcePath( final String path )
    {
        this.urlBuilder.resourcePath( path );
        return this;
    }

    @Override
    public ImageUrlBuilder contentPath( final String contentPath )
    {
        this.urlBuilder.contentPath( contentPath );
        return this;
    }

    @Override
    public ImageUrlBuilder contentPath( final ContentPath contentPath )
    {
        this.urlBuilder.contentPath( contentPath );
        return this;
    }

    @Override
    public String toString()
    {
        return this.urlBuilder.toString();
    }
}
