package com.enonic.xp.style;

import java.util.Objects;

public final class ImageStyle
    extends Style
{
    private final String aspectRatio;

    private final String filter;

    private final String scale;

    private final Integer quality;

    private final String background;

    private ImageStyle( final Builder builder )
    {
        super( builder );
        this.aspectRatio = builder.aspectRatio;
        this.filter = builder.filter;
        this.scale = builder.scale;
        this.quality = builder.quality;
        this.background = builder.background;
    }

    public String getAspectRatio()
    {
        return aspectRatio;
    }

    public String getFilter()
    {
        return filter;
    }

    public String getScale()
    {
        return scale;
    }

    public Integer getQuality()
    {
        return quality;
    }

    public String getBackground()
    {
        return background;
    }

    @Override
    public boolean equals( final Object o )
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
            Objects.equals( scale, that.scale ) &&
            Objects.equals( quality, that.quality ) && Objects.equals( background, that.background );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), aspectRatio, filter, scale, quality, background );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends Style.Builder<Builder, ImageStyle>
    {
        private String aspectRatio;

        private String filter;

        private String scale;

        private Integer quality;

        private String background;

        public Builder scale( final String scale )
        {
            this.scale = scale;
            return this;
        }

        public Builder quality( final Integer quality )
        {
            this.quality = quality;
            return this;
        }

        public Builder background( final String background )
        {
            this.background = background;
            return this;
        }

        public Builder aspectRatio( final String aspectRatio )
        {
            this.aspectRatio = aspectRatio;
            return this;
        }

        public Builder filter( final String filter )
        {
            this.filter = filter;
            return this;
        }

        @Override
        protected Builder self()
        {
            return this;
        }

        @Override
        protected ImageStyle doBuild()
        {
            return new ImageStyle( this );
        }
    }
}
