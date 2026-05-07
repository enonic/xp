package com.enonic.xp.image;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.enonic.xp.util.DoubleHelper;

public final class Cropping
{
    public static final Cropping DEFAULT = Cropping.create().build();

    private final double top;

    private final double left;

    private final double bottom;

    private final double right;

    private Cropping( final Cropping.Builder builder )
    {
        this.top = builder.top;
        this.left = builder.left;
        this.bottom = builder.bottom;
        this.right = builder.right;
        Preconditions.checkArgument( top >= 0, "Cropping top offset value must be positive : %s", top );
        Preconditions.checkArgument( left >= 0, "Cropping left offset value must be positive : %s", left );
        Preconditions.checkArgument( bottom > top, "Cropping bottom value must be bigger than top : %s", bottom );
        Preconditions.checkArgument( right > left, "Cropping right value must be bigger than left : %s", right );
    }

    public boolean isUnmodified()
    {
        return DoubleHelper.fuzzyEquals( top, 0.0 ) &&
            DoubleHelper.fuzzyEquals( left, 0.0 ) &&
            DoubleHelper.fuzzyEquals( bottom, 1.0 ) &&
            DoubleHelper.fuzzyEquals( right, 1.0 );
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

    /**
     * @deprecated Zoom is no longer stored on Cropping. This accessor returns 1.0 for backwards compatibility.
     */
    @Deprecated
    public double zoom()
    {
        return 1.0;
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
        return left == cropping.left && top == cropping.top && right == cropping.right &&
            bottom == cropping.bottom;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( left, top, right, bottom );
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

    public static final class Builder
    {
        private double top;

        private double left;

        private double bottom;

        private double right;

        private Builder()
        {
            this.top = 0;
            this.left = 0;
            this.bottom = 1.0;
            this.right = 1.0;
        }

        private Builder( final Cropping source )
        {
            this.top = source.top;
            this.left = source.left;
            this.bottom = source.bottom;
            this.right = source.right;
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

        /**
         * @deprecated Zoom is no longer stored on Cropping. This setter is a no-op kept for backwards compatibility.
         */
        @Deprecated
        public Builder zoom( final double zoom )
        {
            return this;
        }

        public Cropping build()
        {
            return new Cropping( this );
        }
    }

}
