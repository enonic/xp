package com.enonic.xp.content;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.common.io.ByteSource;

import com.enonic.xp.image.FocalPoint;

import static java.util.Objects.requireNonNull;


public final class UpdateMediaParams
{
    private ContentId content;

    private ContentName name;

    private ByteSource byteSource;

    private @Nullable FocalPoint focalPoint;

    private @Nullable String caption;

    private @Nullable String altText;

    private @Nullable String copyright;

    private List<String> artist = List.of();

    private List<String> tags = List.of();

    public UpdateMediaParams content( final @NonNull ContentId value )
    {
        this.content = value;
        return this;
    }

    public UpdateMediaParams name( final @NonNull String value )
    {
        this.name = ContentName.from( value );
        return this;
    }

    /**
     * @deprecated mime type is auto-detected from the binary; this setter is a no-op.
     */
    @Deprecated
    @SuppressWarnings("unused")
    public UpdateMediaParams mimeType( final String value )
    {
        return this;
    }

    public UpdateMediaParams byteSource( final @NonNull ByteSource value )
    {
        this.byteSource = value;
        return this;
    }

    /**
     * @deprecated use {@link #focalPoint(FocalPoint)} instead.
     */
    @Deprecated
    public UpdateMediaParams focalX( final double focalX )
    {
        final double focalY = focalPoint == null ? FocalPoint.DEFAULT.yOffset() : focalPoint.yOffset();
        this.focalPoint = new FocalPoint( focalX, focalY );
        return this;
    }

    /**
     * @deprecated use {@link #focalPoint(FocalPoint)} instead.
     */
    @Deprecated
    public UpdateMediaParams focalY( final double focalY )
    {
        final double focalX = focalPoint == null ? FocalPoint.DEFAULT.xOffset() : focalPoint.xOffset();
        this.focalPoint = new FocalPoint( focalX, focalY );
        return this;
    }

    public UpdateMediaParams focalPoint( final @Nullable FocalPoint focalPoint )
    {
        this.focalPoint = focalPoint;
        return this;
    }

    public UpdateMediaParams caption( final String caption )
    {
        this.caption = caption;
        return this;
    }

    public UpdateMediaParams artist( final @NonNull List<String> artist )
    {
        this.artist = List.copyOf( artist );
        return this;
    }

    public UpdateMediaParams copyright( final String copyright )
    {
        this.copyright = copyright;
        return this;
    }

    public UpdateMediaParams altText( final String altText )
    {
        this.altText = altText;
        return this;
    }

    public UpdateMediaParams tags( final @NonNull List<String> tags )
    {
        this.tags = List.copyOf( tags );
        return this;
    }

    public void validate()
    {
        requireNonNull( this.content, "content to update is required" );
        requireNonNull( this.name, "name is required" );
        requireNonNull( this.byteSource, "byteSource is required" );
    }

    public ContentId getContent()
    {
        return content;
    }

    public ContentName getName()
    {
        return name;
    }

    public ByteSource getByteSource()
    {
        return byteSource;
    }

    /**
     * @deprecated use {@link #getFocalPoint()} instead.
     */
    @Deprecated
    public double getFocalX()
    {
        return focalPoint == null ? FocalPoint.DEFAULT.xOffset() : focalPoint.xOffset();
    }

    /**
     * @deprecated use {@link #getFocalPoint()} instead.
     */
    @Deprecated
    public double getFocalY()
    {
        return focalPoint == null ? FocalPoint.DEFAULT.yOffset() : focalPoint.yOffset();
    }

    public @Nullable FocalPoint getFocalPoint()
    {
        return focalPoint;
    }

    public String getCaption()
    {
        return caption;
    }

    public String getAltText()
    {
        return altText;
    }

    public List<String> getArtistList()
    {
        return artist;
    }

    public String getCopyright()
    {
        return copyright;
    }

    public List<String> getTagList()
    {
        return tags;
    }
}
