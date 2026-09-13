package com.enonic.xp.image;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.ImageStyleSettings;
import com.enonic.xp.util.BinaryReference;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for reading or generating an image rendition through {@link ImageService}.
 * A style supplies filter, quality and background settings; scale and output MIME type
 * remain separate request parameters.
 */
@NullMarked
public final class ReadImageParams
{
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFFFFFF;

    private final ContentId contentId;

    private final BinaryReference binaryReference;

    private final @Nullable Cropping cropping;

    private final @Nullable ScaleParams scaleParams;

    private final FocalPoint focalPoint;

    private final int scaleSize;

    private final boolean scaleSquare;

    private final boolean scaleWidth;

    private final @Nullable String filterParam;

    private final int backgroundColor;

    private final String mimeType;

    private final int quality;

    private final ImageOrientation orientation;

    private final @Nullable String attachmentSha512;

    private final @Nullable String style;

    private final @Nullable ImageStyle expectedStyle;

    private final boolean cacheOnly;

    private ReadImageParams( final Builder builder )
    {
        this.contentId = builder.contentId;
        this.binaryReference = builder.binaryReference;
        this.cropping = builder.cropping;
        this.scaleParams = builder.scaleParams;
        this.focalPoint = builder.focalPoint != null ? builder.focalPoint : FocalPoint.DEFAULT;
        this.scaleSize = builder.scaleSize;
        this.scaleSquare = builder.scaleSquare;
        this.scaleWidth = builder.scaleWidth;
        this.filterParam = builder.filterParam;
        this.backgroundColor = builder.backgroundColor;
        this.quality = builder.quality;
        this.mimeType = builder.mimeType;
        this.orientation = builder.orientation != null ? builder.orientation : ImageOrientation.TopLeft;
        this.attachmentSha512 = builder.attachmentSha512;
        this.style = builder.style;
        this.expectedStyle = builder.expectedStyle;
        this.cacheOnly = builder.cacheOnly;
    }

    /**
     * Returns the content containing the source attachment.
     *
     * @return the source content identifier
     */
    public ContentId getContentId()
    {
        return contentId;
    }

    /**
     * Returns the attachment to read from the source content.
     *
     * @return the source binary reference
     */
    public BinaryReference getBinaryReference()
    {
        return binaryReference;
    }

    /**
     * Returns the requested source crop.
     *
     * @return the crop
     */
    public @Nullable Cropping getCropping()
    {
        return cropping;
    }

    /**
     * Returns the explicit scaling operation, which takes precedence over the legacy scale options.
     *
     * @return the operation
     */
    public @Nullable ScaleParams getScaleParams()
    {
        return scaleParams;
    }

    /**
     * Returns the focal point used when cropping to the target dimensions.
     *
     * @return the focal point; defaults to {@link FocalPoint#DEFAULT}
     */
    public FocalPoint getFocalPoint()
    {
        return focalPoint;
    }

    /**
     * Returns the legacy pixel size used when no explicit scaling operation is supplied.
     *
     * @return the requested size; zero or a negative value means no scaling
     * @deprecated use {@link #getScaleParams()} for the explicit scaling operation.
     */
    @Deprecated( since = "8.2.0" )
    public int getScaleSize()
    {
        return scaleSize;
    }

    /**
     * Returns whether legacy scaling requests a square crop.
     *
     * @return {@code true} for square scaling; takes precedence over the legacy width option
     * @deprecated use {@link #getScaleParams()} and its operation name.
     */
    @Deprecated( since = "8.2.0" )
    public boolean isScaleSquare()
    {
        return scaleSquare;
    }

    /**
     * Returns whether legacy scaling fixes the image width.
     *
     * @return {@code true} for width scaling when square scaling is not selected
     * @deprecated use {@link #getScaleParams()} and its operation name.
     */
    @Deprecated( since = "8.2.0" )
    public boolean isScaleWidth()
    {
        return scaleWidth;
    }

    /**
     * Returns the explicit filter specification for an unstyled request.
     *
     * @return the filter specification
     */
    public @Nullable String getFilterParam()
    {
        return filterParam;
    }

    /**
     * Returns the explicit RGB background value, before any style is resolved.
     *
     * @return the RGB value in {@code 0x000000} through {@code 0xFFFFFF}; defaults to white
     */
    public int getBackgroundColor()
    {
        return backgroundColor;
    }

    /**
     * Returns the requested output MIME type.
     *
     * @return the output MIME type, such as {@code image/jpeg} or {@code image/avif}
     */
    public String getMimeType()
    {
        return mimeType;
    }

    /**
     * Returns the explicit encoder quality, before any style is resolved.
     * For JPEG, PNG and GIF output, zero requests the encoder's default quality.
     *
     * @return a value from 0 through 100; defaults to 0
     */
    public int getQuality()
    {
        return quality;
    }

    /**
     * Returns the orientation to apply before scaling.
     *
     * @return the source orientation; defaults to {@link ImageOrientation#TopLeft}
     */
    public ImageOrientation getOrientation()
    {
        return orientation;
    }

    /**
     * Returns the source checksum supplied for cache lookup.
     *
     * @return the hexadecimal SHA-512 checksum
     */
    public @Nullable String getAttachmentSha512()
    {
        return attachmentSha512;
    }

    /**
     * Returns whether this request is restricted to an existing cached rendition.
     *
     * @return {@code true} to prohibit generation and cache population on a miss
     */
    public boolean isCacheOnly()
    {
        return cacheOnly;
    }

    /**
     * Returns the style snapshot whose effective settings must still match at processing time.
     *
     * @return the expected style
     */
    public @Nullable ImageStyle getExpectedStyle()
    {
        return expectedStyle;
    }

    /**
     * Returns the predefined style to resolve when reading the image.
     *
     * @return the fully qualified {@code application:name} key
     */
    public @Nullable String getStyle()
    {
        return style;
    }

    /**
     * Creates a builder for an image request.
     *
     * @return a new builder
     */
    public static Builder newImageParams()
    {
        return new Builder();
    }

    /**
     * Builds an image request. By default, cache misses may generate a rendition,
     * quality is 0, the background is white and no scaling is requested.
     */
    public static final class Builder
    {
        private @Nullable ContentId contentId;

        private @Nullable BinaryReference binaryReference;

        private @Nullable Cropping cropping;

        private @Nullable ScaleParams scaleParams;

        private @Nullable FocalPoint focalPoint;

        private int scaleSize;

        private boolean scaleSquare;

        private boolean scaleWidth;

        private @Nullable String filterParam;

        private int backgroundColor = DEFAULT_BACKGROUND_COLOR;

        private @Nullable String mimeType;

        private @Nullable ImageOrientation orientation;

        /**
         * Explicit quality value from 0 through 100; defaults to 0.
         *
         * @deprecated use {@link #quality(int)} to record an explicit quality selection, including zero.
         */
        @Deprecated( since = "8.2.0" )
        public int quality;

        private @Nullable String attachmentSha512;

        private @Nullable String style;

        private @Nullable ImageStyle expectedStyle;

        private boolean cacheOnly;

        private boolean qualitySet;

        private boolean backgroundSet;

        /**
         * Controls whether only an existing cached rendition may be read.
         * A cache-only request requires {@link #attachmentSha512(String) a source checksum};
         * a miss fails without reading source bytes or populating the cache.
         *
         * @param cacheOnly {@code true} to prohibit generation; defaults to {@code false}
         * @return this builder
         */
        public Builder cacheOnly( final boolean cacheOnly )
        {
            this.cacheOnly = cacheOnly;
            return this;
        }

        /**
         * Sets a style snapshot to detect a change between URL validation and processing.
         * Only effective processing settings are compared. Supplying a snapshot requires a style key.
         *
         * @param expectedStyle the expected style
         * @return this builder
         */
        public Builder expectedStyle( final @Nullable ImageStyle expectedStyle )
        {
            this.expectedStyle = expectedStyle;
            return this;
        }

        /**
         * Selects a predefined style whose processing settings are resolved by the image service.
         * A styled request cannot also specify filters, quality or background overrides.
         *
         * @param style the {@code application:name} key; an empty key selects an unstyled request
         * @return this builder
         */
        public Builder style( final @Nullable String style )
        {
            this.style = style == null || style.isEmpty() ? null : style;
            return this;
        }

        private Builder()
        {
        }

        /**
         * Identifies the content containing the source image.
         *
         * @param contentId the required source content identifier
         * @return this builder
         */
        public Builder contentId( @Nullable ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        /**
         * Identifies the source attachment within the content.
         *
         * @param binaryReference the required binary reference
         * @return this builder
         */
        public Builder binaryReference( @Nullable BinaryReference binaryReference )
        {
            this.binaryReference = binaryReference;
            return this;
        }

        /**
         * Sets the crop to apply to the source image.
         *
         * @param cropping the crop
         * @return this builder
         */
        public Builder cropping( @Nullable Cropping cropping )
        {
            this.cropping = cropping;
            return this;
        }

        /**
         * Sets an explicit scaling operation, overriding the legacy size and shape options.
         *
         * @param scaleParams the operation
         * @return this builder
         */
        public Builder scaleParams( @Nullable ScaleParams scaleParams )
        {
            this.scaleParams = scaleParams;
            return this;
        }

        /**
         * Sets the focal point used for cropping.
         *
         * @param focalPoint the focal point
         * @return this builder
         */
        public Builder focalPoint( @Nullable FocalPoint focalPoint )
        {
            this.focalPoint = focalPoint;
            return this;
        }

        /**
         * Sets the legacy pixel size used only when no explicit scaling operation is supplied.
         *
         * @param scaleSize the size in pixels; zero or a negative value disables legacy scaling
         * @return this builder
         * @deprecated use {@link #scaleParams(ScaleParams)} with a {@code max}, {@code width} or {@code square}
         *     operation and one pixel-size argument. Use {@link ScaleParams#NO_SCALE} to disable scaling.
         */
        @Deprecated( since = "8.2.0" )
        public Builder scaleSize( int scaleSize )
        {
            this.scaleSize = scaleSize;
            return this;
        }

        /**
         * Selects square cropping for legacy scaling when the legacy size is positive.
         *
         * @param scaleSquare {@code true} for square scaling; takes precedence over {@link #scaleWidth(boolean)}
         * @return this builder
         * @deprecated use {@link #scaleParams(ScaleParams)} with
         *     {@code new ScaleParams("square", new Object[]{size})} for a square crop.
         */
        @Deprecated( since = "8.2.0" )
        public Builder scaleSquare( boolean scaleSquare )
        {
            this.scaleSquare = scaleSquare;
            return this;
        }

        /**
         * Selects width scaling for legacy scaling when the size is positive and square scaling is disabled.
         *
         * @param scaleWidth {@code true} for a fixed width; otherwise the legacy size bounds both dimensions
         * @return this builder
         * @deprecated use {@link #scaleParams(ScaleParams)} with
         *     {@code new ScaleParams("width", new Object[]{size})} for a fixed width,
         *     or {@code new ScaleParams("max", new Object[]{size})} to bound both dimensions.
         */
        @Deprecated( since = "8.2.0" )
        public Builder scaleWidth( boolean scaleWidth )
        {
            this.scaleWidth = scaleWidth;
            return this;
        }

        /**
         * Sets the filter specification for an unstyled request.
         *
         * @param filterParam the filter specification
         * @return this builder
         */
        public Builder filterParam( @Nullable String filterParam )
        {
            this.filterParam = filterParam;
            return this;
        }

        /**
         * Sets the RGB background used when flattening transparency for JPEG or GIF output.
         *
         * @param backgroundColor the RGB value from {@code 0x000000} through {@code 0xFFFFFF}; defaults to white
         * @return this builder
         */
        public Builder backgroundColor( int backgroundColor )
        {
            this.backgroundSet = true;
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * Selects the output format independently of scaling and style.
         * Supported MIME types are {@code image/jpeg}, {@code image/png}, {@code image/gif},
         * {@code image/webp} and {@code image/avif}; the latter two require a style.
         *
         * @param mimeType the required output MIME type
         * @return this builder
         */
        public Builder mimeType( final @Nullable String mimeType )
        {
            this.mimeType = mimeType;
            return this;
        }

        /**
         * Sets the encoder quality for an unstyled request.
         * For JPEG, PNG and GIF output, zero requests the encoder's default quality.
         *
         * @param quality the quality from 0 through 100
         * @return this builder
         */
        public Builder quality( int quality )
        {
            this.qualitySet = true;
            this.quality = quality;
            return this;
        }

        /**
         * Sets the source orientation to apply before scaling.
         *
         * @param orientation the orientation
         * @return this builder
         */
        public Builder orientation( @Nullable ImageOrientation orientation )
        {
            this.orientation = orientation;
            return this;
        }

        /**
         * Supplies the source checksum for cache lookup. Cache-only requests require this value.
         * Generation verifies source bytes before publishing a new cache entry.
         *
         * @param attachmentSha512 the hexadecimal SHA-512 checksum
         * @return this builder
         */
        public Builder attachmentSha512( @Nullable String attachmentSha512 )
        {
            this.attachmentSha512 = attachmentSha512;
            return this;
        }

        /**
         * Validates the supplied request fields and creates the image request.
         * Style resolution, scale/filter validation and cache-only checksum requirements
         * are checked by the image service when the request is read.
         *
         * @return a new image request
         * @throws NullPointerException if content identifier, binary reference or output MIME type is missing
         * @throws IllegalArgumentException if quality or background is out of range, processing overrides
         *     accompany a style, or an expected style is supplied without a style key
         */
        public ReadImageParams build()
        {
            ImageStyleSettings.checkOverrides( style, qualitySet || quality != 0 || backgroundSet || filterParam != null );
            Preconditions.checkArgument( expectedStyle == null || style != null, "expectedStyle requires a style key" );
            requireNonNull( contentId, "contentId is required" );
            requireNonNull( binaryReference, "binaryReference is required" );
            requireNonNull( mimeType, "mimeType is required" );
            Preconditions.checkArgument( quality >= 0 && quality <= 100, "Quality out of bounds 0-100" );
            Preconditions.checkArgument( backgroundColor >= 0 && backgroundColor <= 0xFFFFFF, "Background color out of bounds 0-0xFFFFFF" );
            return new ReadImageParams( this );
        }
    }
}
