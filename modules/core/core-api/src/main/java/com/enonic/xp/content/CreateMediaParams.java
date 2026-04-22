package com.enonic.xp.content;


import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.common.io.ByteSource;

import com.enonic.xp.image.FocalPoint;

import static java.util.Objects.requireNonNull;


public final class CreateMediaParams
{
    private ContentPath parent;

    private ContentName name;

    private ByteSource byteSource;

    private @Nullable FocalPoint focalPoint;

    private @Nullable String caption;

    private @Nullable String altText;

    private @Nullable String copyright;

    private List<String> artist = List.of();

    private List<String> tags = List.of();

    public CreateMediaParams parent( final @NonNull ContentPath value )
    {
        this.parent = value;
        return this;
    }

    public CreateMediaParams name( final @NonNull String value )
    {
        this.name = ContentName.from( value );
        return this;
    }

    public CreateMediaParams name( final @NonNull ContentName value )
    {
        this.name = value;
        return this;
    }

    /**
     * @deprecated mime type is auto-detected from the binary; this setter is a no-op.
     */
    @Deprecated
    @SuppressWarnings("unused")
    public CreateMediaParams mimeType( final String value )
    {
        return this;
    }

    public CreateMediaParams byteSource( final @NonNull ByteSource value )
    {
        this.byteSource = value;
        return this;
    }

    /**
     * @deprecated use {@link #focalPoint(FocalPoint)} instead.
     */
    @Deprecated
    public CreateMediaParams focalX( final double focalX )
    {
        final double focalY = focalPoint == null ? FocalPoint.DEFAULT.yOffset() : focalPoint.yOffset();
        this.focalPoint = new FocalPoint( focalX, focalY );
        return this;
    }

    /**
     * @deprecated use {@link #focalPoint(FocalPoint)} instead.
     */
    @Deprecated
    public CreateMediaParams focalY( final double focalY )
    {
        final double focalX = focalPoint == null ? FocalPoint.DEFAULT.xOffset() : focalPoint.xOffset();
        this.focalPoint = new FocalPoint( focalX, focalY );
        return this;
    }

    public CreateMediaParams focalPoint( final @Nullable FocalPoint focalPoint )
    {
        this.focalPoint = focalPoint;
        return this;
    }

    public CreateMediaParams caption( final String caption )
    {
        this.caption = caption;
        return this;
    }

    public CreateMediaParams altText( final String altText )
    {
        this.altText = altText;
        return this;
    }

    /**
     * @deprecated use {@link #artist(List)} instead.
     */
    @Deprecated
    public CreateMediaParams artist( final String artist )
    {
        this.artist = artist == null ? List.of() : List.of( artist );
        return this;
    }

    public CreateMediaParams artist( final @NonNull List<String> artist )
    {
        this.artist = List.copyOf( artist );
        return this;
    }

    public CreateMediaParams copyright( final String copyright )
    {
        this.copyright = copyright;
        return this;
    }

    /**
     * @deprecated use {@link #tags(List)} instead.
     */
    @Deprecated
    public CreateMediaParams tags( final String tags )
    {
        this.tags = tags == null ? List.of() : List.of( tags );
        return this;
    }

    public CreateMediaParams tags( final @NonNull List<String> tags )
    {
        this.tags = List.copyOf( tags );
        return this;
    }

    public void validate()
    {
        requireNonNull( this.parent, "parent is required" );
        requireNonNull( this.name, "name is required" );
        requireNonNull( this.byteSource, "byteSource is required" );
    }

    public ContentPath getParent()
    {
        return parent;
    }

    public ContentName getName()
    {
        return name;
    }

    public ByteSource getByteSource()
    {
        return byteSource;
    }

    public @Nullable FocalPoint getFocalPoint()
    {
        return focalPoint;
    }

    public @Nullable String getCaption()
    {
        return caption;
    }

    public @Nullable String getAltText()
    {
        return altText;
    }

    public List<String> getArtistList()
    {
        return artist;
    }

    public @Nullable String getCopyright()
    {
        return copyright;
    }

    public List<String> getTagList()
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
        return Objects.equals( focalPoint, that.focalPoint ) && Objects.equals( parent, that.parent ) &&
            Objects.equals( name, that.name ) && Objects.equals( byteSource, that.byteSource ) && Objects.equals( caption, that.caption ) &&
            Objects.equals( altText, that.altText ) && Objects.equals( artist, that.artist ) &&
            Objects.equals( copyright, that.copyright ) && Objects.equals( tags, that.tags );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( parent, name, byteSource, focalPoint, caption, artist, copyright, tags );
    }
}
