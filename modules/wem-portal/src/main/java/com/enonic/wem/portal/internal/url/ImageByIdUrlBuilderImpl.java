package com.enonic.wem.portal.internal.url;


import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.portal.url.ImageByIdUrlBuilder;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;

public final class ImageByIdUrlBuilderImpl
    implements ImageByIdUrlBuilder
{
    private final UrlBuilder urlBuilder;

    private static final String IMAGE_RESOURCE_TYPE = "image";

    private ContentId imageId;

    private String background;

    private Integer quality;

    private List<String> filters;

    public ImageByIdUrlBuilderImpl( final String baseUrl )
    {
        background = "";
        quality = null;
        this.filters = Lists.newArrayList();
        this.urlBuilder = new UrlBuilder( baseUrl );
        this.urlBuilder.beforeBuildUrl( ( urlBuilder ) -> {
            urlBuilder.resourceType( IMAGE_RESOURCE_TYPE );
            urlBuilder.resourcePath( "id/" + this.imageId );

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
    public ImageByIdUrlBuilderImpl imageContent( final ContentId id )
    {
        this.imageId = id;
        return this;
    }

    @Override
    public ImageByIdUrlBuilder imageContent( final String id )
    {
        this.imageId = ContentId.from( id );
        return this;
    }

    @Override
    public ImageByIdUrlBuilderImpl filter( final String... filters )
    {
        Collections.addAll( this.filters, filters );
        return this;
    }

    @Override
    public ImageByIdUrlBuilderImpl background( final String backgroundColor )
    {
        this.background = nullToEmpty( backgroundColor );
        return this;
    }

    @Override
    public ImageByIdUrlBuilderImpl quality( final int quality )
    {
        checkArgument( quality > 0, "Image Quality must be between 1 and 100. Value: %s", quality );
        checkArgument( quality <= 100, "Image Quality must be between 1 and 100. Value: %s", quality );
        this.quality = quality;
        return this;
    }

    @Override
    public ImageByIdUrlBuilderImpl mode( final String mode )
    {
        this.urlBuilder.mode( mode );
        return this;
    }

    @Override
    public ImageByIdUrlBuilderImpl workspace( final String workspace )
    {
        this.urlBuilder.workspace( workspace );
        return this;
    }

    @Override
    public ImageByIdUrlBuilderImpl resourcePath( final String path )
    {
        this.urlBuilder.resourcePath( path );
        return this;
    }

    @Override
    public ImageByIdUrlBuilderImpl contentPath( final String contentPath )
    {
        this.urlBuilder.contentPath( contentPath );
        return this;
    }

    @Override
    public ImageByIdUrlBuilderImpl contentPath( final ContentPath contentPath )
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
