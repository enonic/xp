package com.enonic.xp.content;


import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

@Beta
public final class UpdateMediaParams
{
    private ContentId content;

    private String name;

    private String mimeType;

    private ByteSource inputStream;

    private double focalX = 0.5;

    private double focalY = 0.5;

    private String caption;

    private String artist;

    private String copyright;

    private String tags;

    public UpdateMediaParams content( final ContentId value )
    {
        this.content = value;
        return this;
    }

    public UpdateMediaParams name( final String value )
    {
        this.name = value;
        return this;
    }

    public UpdateMediaParams mimeType( final String value )
    {
        this.mimeType = value;
        return this;
    }

    public UpdateMediaParams byteSource( final ByteSource value )
    {
        this.inputStream = value;
        return this;
    }

    public UpdateMediaParams focalX( final double focalX )
    {
        this.focalX = focalX;
        return this;
    }

    public UpdateMediaParams focalY( final double focalY )
    {
        this.focalY = focalY;
        return this;
    }

    public UpdateMediaParams caption( final String caption )
    {
        this.caption = caption;
        return this;
    }

    public UpdateMediaParams artist( final String artist )
    {
        this.artist = artist;
        return this;
    }

    public UpdateMediaParams copyright( final String copyright )
    {
        this.copyright = copyright;
        return this;
    }

    public UpdateMediaParams tags( final String tags )
    {
        this.tags = tags;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.content, "content to update cannot be null." );
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.inputStream, "byteSource cannot be null" );
    }

    public ContentId getContent()
    {
        return content;
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
        return inputStream;
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
        final UpdateMediaParams params = (UpdateMediaParams) o;
        return Double.compare( params.focalX, focalX ) == 0 && Double.compare( params.focalY, focalY ) == 0 &&
            Objects.equals( content, params.content ) && Objects.equals( name, params.name ) &&
            Objects.equals( mimeType, params.mimeType ) && Objects.equals( inputStream, params.inputStream ) &&
            Objects.equals( caption, params.caption ) && Objects.equals( artist, params.artist ) &&
            Objects.equals( copyright, params.copyright ) && Objects.equals( tags, params.tags );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( content, name, mimeType, inputStream, focalX, focalY, caption, artist, copyright, tags );
    }
}
