package com.enonic.wem.portal.internal.script.lib;


import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.ContentId;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;

public final class ImageUrlBuilder
    extends PortalUrlBuilder<ImageUrlBuilder>
{
    private static final String IMAGE_RESOURCE_TYPE = "image";

    private ContentId imageId;

    private String background;

    private Integer quality;

    private List<String> filters;

    private ImageUrlBuilder( final String baseUrl )
    {
        super( baseUrl );
        background = "";
        quality = null;
        this.filters = Lists.newArrayList();
    }

    public ImageUrlBuilder imageContent( final ContentId id )
    {
        this.imageId = id;
        return this;
    }

    public ImageUrlBuilder filter( final List<String> filter )
    {
        this.filters.addAll( filter );
        return this;
    }

    public ImageUrlBuilder filter( final String... filters )
    {
        Collections.addAll( this.filters, filters );
        return this;
    }

    public ImageUrlBuilder background( final String backgroundColor )
    {
        this.background = nullToEmpty( backgroundColor );
        return this;
    }

    public ImageUrlBuilder quality( final int quality )
    {
        checkArgument( quality > 0, "Image Quality must be between 1 and 100. Value: %s", quality );
        checkArgument( quality <= 100, "Image Quality must be between 1 and 100. Value: %s", quality );
        this.quality = quality;
        return this;
    }

    protected void beforeBuildUrl()
    {
        resourceType( IMAGE_RESOURCE_TYPE );

        if ( this.imageId != null )
        {
            resourcePath( "id/" + this.imageId );
        }

        if ( !filters.isEmpty() )
        {
            final String filtersStr = Joiner.on( ";" ).skipNulls().join( filters );
            param( "filter", filtersStr );
        }
        if ( !background.isEmpty() )
        {
            param( "background", background );
        }
        if ( quality != null )
        {
            param( "quality", quality );
        }
    }

    public static ImageUrlBuilder createImageUrl( final String baseUrl )
    {
        return new ImageUrlBuilder( baseUrl );
    }
}
