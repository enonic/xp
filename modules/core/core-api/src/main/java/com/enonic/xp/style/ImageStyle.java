package com.enonic.xp.style;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A predefined image style combining editor metadata with optional processing settings.
 * Scale and output format belong to the image request, not the style. Processing settings
 * are validated and defaulted by {@link ImageStyleSettings#from(ImageStyle)}.
 */
@NullMarked
public final class ImageStyle
    extends Style
{
    private final @Nullable String aspectRatio;

    private final @Nullable String filter;

    private final @Nullable Integer quality;

    private final @Nullable String background;

    /**
     * Creates an image style with the selected metadata and settings.
     *
     * @param builder the style builder
     */
    private ImageStyle( final Builder builder )
    {
        super( builder );
        this.aspectRatio = builder.aspectRatio;
        this.filter = builder.filter;
        this.quality = builder.quality;
        this.background = builder.background;
    }

    /**
     * Returns the aspect-ratio hint for width or height scaling.
     *
     * @return the {@code width:height} ratio
     */
    public @Nullable String getAspectRatio()
    {
        return aspectRatio;
    }

    /**
     * Returns the configured image filter specification.
     *
     * @return the filter specification
     */
    public @Nullable String getFilter()
    {
        return filter;
    }

    /**
     * Returns the configured encoder quality before defaults are applied.
     *
     * @return the quality
     */
    public @Nullable Integer getQuality()
    {
        return quality;
    }

    /**
     * Returns the configured RGB background before defaults are applied.
     *
     * @return the hexadecimal RGB color
     */
    public @Nullable String getBackground()
    {
        return background;
    }

    /**
     * Compares all inherited style metadata and image processing settings.
     *
     * @param o the object to compare
     * @return {@code true} if both objects describe the same metadata and settings
     */
    @Override
    public boolean equals( final @Nullable Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final ImageStyle that = (ImageStyle) o;
        return Objects.equals( aspectRatio, that.aspectRatio ) && Objects.equals( filter, that.filter ) &&
            Objects.equals( quality, that.quality ) && Objects.equals( background, that.background );
    }

    /**
     * Computes a hash from the inherited style metadata and image processing settings.
     *
     * @return the style hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), aspectRatio, filter, quality, background );
    }

    /**
     * Creates a builder for an image style.
     *
     * @return a new style builder
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builds an image style. Unspecified processing settings are resolved by {@link ImageStyleSettings}.
     */
    public static final class Builder
        extends Style.Builder<Builder, ImageStyle>
    {
        private @Nullable String aspectRatio;

        private @Nullable String filter;

        private @Nullable Integer quality;

        private @Nullable String background;

        /**
         * Sets the encoder quality, validated when processing settings are resolved.
         *
         * @param quality a value from 0 through 100
         * @return this builder
         */
        public Builder quality( final @Nullable Integer quality )
        {
            this.quality = quality;
            return this;
        }

        /**
         * Sets the RGB background used when flattening transparency.
         *
         * @param background one to six hexadecimal digits, optionally prefixed by {@code 0x}
         * @return this builder
         */
        public Builder background( final @Nullable String background )
        {
            this.background = background;
            return this;
        }

        /**
         * Sets an aspect-ratio hint for width or height scaling.
         * Other scaling operations retain their own geometry; this does not prescribe a URL scale.
         *
         * @param aspectRatio a positive integer {@code width:height} ratio
         * @return this builder
         */
        public Builder aspectRatio( final @Nullable String aspectRatio )
        {
            this.aspectRatio = aspectRatio;
            return this;
        }

        /**
         * Sets the image filter specification.
         *
         * @param filter the filter specification
         * @return this builder
         */
        public Builder filter( final @Nullable String filter )
        {
            this.filter = filter;
            return this;
        }

        /**
         * Returns this builder for inherited fluent configuration methods.
         *
         * @return this builder
         */
        @Override
        protected Builder self()
        {
            return this;
        }

        /**
         * Creates an image style with the selected metadata and settings.
         * Processing settings are validated separately by {@link ImageStyleSettings#from(ImageStyle)}.
         *
         * @return a new image style
         */
        @Override
        protected ImageStyle doBuild()
        {
            return new ImageStyle( this );
        }
    }
}
