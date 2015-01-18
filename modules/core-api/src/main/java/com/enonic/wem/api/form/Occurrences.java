package com.enonic.wem.api.form;


import java.util.Objects;

import com.google.common.base.Preconditions;

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
        return Objects.equals( this.maximum, that.maximum ) && Objects.equals( this.minimum, that.minimum );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.maximum, this.minimum );
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append( "min=" ).append( minimum ).append( ", max=" ).append( maximum );
        return s.toString();
    }

    public static Builder newOccurrences()
    {
        return new Builder();
    }

    public static Builder newOccurrences( final Occurrences occurrences )
    {
        return new Builder( occurrences );
    }

    public static class Builder
    {
        private int minimum;

        private int maximum;

        private Builder()
        {
        }

        private Builder( final Occurrences occurrences )
        {
            this.minimum = occurrences.getMinimum();
            this.maximum = occurrences.getMaximum();
        }

        public Builder minimum( final int minimum )
        {
            this.minimum = minimum;
            return this;
        }

        public Builder maximum( final int maximum )
        {
            this.maximum = maximum;
            return this;
        }

        public Occurrences build()
        {
            return new Occurrences( minimum, maximum );
        }
    }

}
