package com.enonic.xp.image;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public final class Cropping
{

    private final double top;

    private final double left;

    private final double bottom;

    private final double right;

    private final double zoom;

    private Cropping( final Cropping.Builder builder )
    {
        this.top = builder.top;
        this.left = builder.left;
        this.bottom = builder.bottom;
        this.right = builder.right;
        this.zoom = builder.zoom;
        Preconditions.checkArgument( top >= 0, "Cropping top offset value must be positive : %s", top );
        Preconditions.checkArgument( left >= 0, "Cropping left offset value must be positive : %s", left );
        Preconditions.checkArgument( bottom > top, "Cropping bottom value must be bigger than top : %s", bottom );
        Preconditions.checkArgument( right > left, "Cropping right value must be bigger than left : %s", right );
    }

    public double top()
    {
        return top;
    }

    public double left()
    {
        return left;
    }

    public double bottom()
    {
        return bottom;
    }

    public double right()
    {
        return right;
    }

    public double zoom()
    {
        return zoom;
    }

    public double width()
    {
        return right - left;
    }

    public double height()
    {
        return bottom - top;
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
        final Cropping cropping = (Cropping) o;
        return Objects.equal( left, cropping.left ) &&
            Objects.equal( top, cropping.top ) &&
            Objects.equal( right, cropping.right ) &&
            Objects.equal( bottom, cropping.bottom );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( left, top, right, bottom );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "top", top ).
            add( "left", left ).
            add( "bottom", bottom ).
            add( "right", right ).
            toString();
    }

    public static Cropping.Builder create()
    {
        return new Cropping.Builder();
    }

    public static Cropping.Builder copyOf( final Cropping cropping )
    {
        return new Cropping.Builder( cropping );
    }

    public static class Builder
    {
        private double top;

        private double left;

        private double bottom;

        private double right;

        private double zoom;

        private Builder()
        {
            this.top = 0;
            this.left = 0;
            this.bottom = 0;
            this.right = 0;
            this.zoom = 0;
        }

        private Builder( final Cropping source )
        {
            this.top = source.top;
            this.left = source.left;
            this.bottom = source.bottom;
            this.right = source.right;
            this.zoom = source.zoom;
        }

        public Builder left( final double left )
        {
            this.left = left;
            return this;
        }

        public Builder top( final double top )
        {
            this.top = top;
            return this;
        }

        public Builder right( final double right )
        {
            this.right = right;
            return this;
        }

        public Builder bottom( final double bottom )
        {
            this.bottom = bottom;
            return this;
        }

        public Builder zoom( final double zoom )
        {
            this.zoom = zoom;
            return this;
        }

        public Cropping build()
        {
            return new Cropping( this );
        }
    }

}
