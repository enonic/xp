package com.enonic.xp.style;

import java.util.Objects;

public final class ImageStyle
    extends Style
{
    private final String aspectRatio;

    private final String filter;

    private ImageStyle( final Builder builder )
    {
        super( builder );
        this.aspectRatio = builder.aspectRatio;
        this.filter = builder.filter;
    }

    public String getAspectRatio()
    {
        return aspectRatio;
    }

    public String getFilter()
    {
        return filter;
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
        return Objects.equals( aspectRatio, that.aspectRatio ) && Objects.equals( filter, that.filter );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), aspectRatio, filter );
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
