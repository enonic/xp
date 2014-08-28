package com.enonic.wem.portal.url2;

import java.util.Map;

import com.google.common.base.Joiner;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentName;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;

public final class ImageUrlBuilder
    extends PortalUrlBuilder<ImageUrlBuilder>
{
    private String background;

    private Integer quality;

    private ContentId imageId;

    private ContentName imageName;

    private String[] filters;

    public ImageUrlBuilder()
    {
        this.filters = new String[0];
    }

    public ImageUrlBuilder imageName( final String imageName )
    {
        return imageName( isNullOrEmpty( imageName ) ? null : ContentName.from( imageName ) );
    }

    public ImageUrlBuilder imageId( final String imageId )
    {
        return imageId( isNullOrEmpty( imageId ) ? null : ContentId.from( imageId ) );
    }

    public ImageUrlBuilder imageName( final ContentName imageName )
    {
        this.imageName = imageName;
        return this;
    }

    public ImageUrlBuilder imageId( final ContentId imageId )
    {
        this.imageId = imageId;
        return this;
    }

    public ImageUrlBuilder filter( final String... filters )
    {
        this.filters = filters;
        return this;
    }

    public ImageUrlBuilder background( final String backgroundColor )
    {
        this.background = emptyToNull( backgroundColor );
        return this;
    }

    public ImageUrlBuilder quality( final int quality )
    {
        checkArgument( quality > 0, "Image Quality must be between 1 and 100. Value: %s", quality );
        checkArgument( quality <= 100, "Image Quality must be between 1 and 100. Value: %s", quality );
        this.quality = quality;
        return this;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Map<String, String> params )
    {
        super.buildUrl( url, params );

        appendPart( url, "_" );
        appendPart( url, "image" );

        if ( this.imageName != null )
        {
            appendPart( url, this.imageName.toString() );
        }
        else if ( this.imageId != null )
        {
            appendPart( url, "id" );
            appendPart( url, this.imageId.toString() );
        }

        if ( this.filters.length > 0 )
        {
            final String filtersStr = Joiner.on( ";" ).skipNulls().join( this.filters );
            params.put( "filter", filtersStr );
        }

        if ( this.background != null )
        {
            params.put( "background", this.background );
        }

        if ( this.quality != null )
        {
            params.put( "quality", String.valueOf( this.quality ) );
        }
    }
}
