package com.enonic.xp.content;


import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class CreateMediaParams
{
    private ContentPath parent;

    private String name;

    private String mimeType;

    private ByteSource byteSource;

    private double focalX = 0.5;

    private double focalY = 0.5;

    private String caption = "";

    private String artist = "";

    private String copyright = "";

    private String tags = "";

    public CreateMediaParams parent( final ContentPath value )
    {
        this.parent = value;
        return this;
    }

    public CreateMediaParams name( final String value )
    {
        this.name = value;
        return this;
    }

    public CreateMediaParams mimeType( final String value )
    {
        this.mimeType = value;
        return this;
    }

    public CreateMediaParams byteSource( final ByteSource value )
    {
        this.byteSource = value;
        return this;
    }

    public CreateMediaParams focalX( final double focalX )
    {
        this.focalX = focalX;
        return this;
    }

    public CreateMediaParams focalY( final double focalY )
    {
        this.focalY = focalY;
        return this;
    }

    public CreateMediaParams caption( final String caption )
    {
        this.caption = caption;
        return this;
    }

    public CreateMediaParams artist( final String artist )
    {
        this.artist = artist;
        return this;
    }

    public CreateMediaParams copyright( final String copyright )
    {
        this.copyright = copyright;
        return this;
    }

    public CreateMediaParams tags( final String tags )
    {
        this.tags = tags;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.parent, "parent cannot be null. Use ContentPath.ROOT when content has no parent." );
        Preconditions.checkArgument( this.parent.isAbsolute(), "parent must be absolute: " + this.parent );
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.byteSource, "byteSource cannot be null" );
    }

    public ContentPath getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public ByteSource getByteSource()
    {
        return byteSource;
    }

    public double getFocalX()
    {
        return focalX;
    }

    public double getFocalY()
    {
        return focalY;
    }

    public String getCaption()
    {
        return caption;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getCopyright()
    {
        return copyright;
    }

    public String getTags()
    {
        return tags;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final CreateMediaParams that = (CreateMediaParams) o;
        return Double.compare( that.focalX, focalX ) == 0 && Double.compare( that.focalY, focalY ) == 0 &&
            Objects.equals( parent, that.parent ) && Objects.equals( name, that.name ) && Objects.equals( mimeType, that.mimeType ) &&
            Objects.equals( byteSource, that.byteSource ) && Objects.equals( caption, that.caption ) &&
            Objects.equals( artist, that.artist ) && Objects.equals( copyright, that.copyright ) && Objects.equals( tags, that.tags );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( parent, name, mimeType, byteSource, focalX, focalY, caption, artist, copyright, tags );
    }
}
