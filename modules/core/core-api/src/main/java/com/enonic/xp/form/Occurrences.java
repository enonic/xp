package com.enonic.xp.form;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class Occurrences
{
    private final int minimum;

    /**
     * Zero means unlimited;
     */
    private final int maximum;

    public Occurrences( final int minimum, final int maximum )
    {
        Preconditions.checkArgument( minimum >= 0 );
        Preconditions.checkArgument( maximum >= 0 );

        this.minimum = minimum;
        this.maximum = maximum;
    }

    public int getMinimum()
    {
        return minimum;
    }

    public int getMaximum()
    {
        return maximum;
    }

    public boolean impliesRequired()
    {
        return minimum > 0;
    }

    public boolean isMultiple()
    {
        return maximum == 0 || maximum > 1;
    }

    public boolean isUnlimited()
    {
        return maximum == 0;
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

        final Occurrences that = (Occurrences) o;
        return this.maximum == that.maximum && this.minimum == that.minimum;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.maximum, this.minimum );
    }

    @Override
    public String toString()
    {
        return "min=" + minimum + ", max=" + maximum;
    }

    public static Occurrences create( final int minimum, final int maximum )
    {
        return new Occurrences( minimum, maximum );
    }
}


