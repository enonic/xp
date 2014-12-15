package com.enonic.wem.portal.url;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentName;

import static com.google.common.base.Strings.emptyToNull;

public final class ImageUrlBuilder
    extends PortalUrlBuilder<ImageUrlBuilder>
{
    private String background;

    private String quality;

    private String imageId;

    private String imageName;

    private String filter;

    public ImageUrlBuilder imageId( final String value )
    {
        this.imageId = emptyToNull( value );
        return this;
    }

    public ImageUrlBuilder imageId( final ContentId value )
    {
        return imageId( value != null ? value.toString() : null );
    }

    public ImageUrlBuilder imageName( final String value )
    {
        this.imageName = emptyToNull( value );
        return this;
    }

    public ImageUrlBuilder imageName( final ContentName value )
    {
        return imageName( value != null ? value.toString() : null );
    }

    public ImageUrlBuilder filter( final String value )
    {
        this.filter = emptyToNull( value );
        return this;
    }

    public ImageUrlBuilder filters( final String... value )
    {
        this.filter = Joiner.on( ";" ).skipNulls().join( value );
        return this;
    }

    public ImageUrlBuilder background( final String value )
    {
        this.background = emptyToNull( value );
        return this;
    }

    public ImageUrlBuilder quality( final int quality )
    {
        return quality( String.valueOf( quality ) );
    }

    public ImageUrlBuilder quality( final String value )
    {
        this.quality = emptyToNull( value );
        return this;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );

        appendPart( url, "_" );
        appendPart( url, "image" );

        if ( this.imageName != null )
        {
            appendPart( url, this.imageName );
        }
        else if ( this.imageId != null )
        {
            appendPart( url, "id" );
            appendPart( url, this.imageId );
        }

        if ( this.filter != null )
        {
            params.put( "filter", this.filter );
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
