package com.enonic.wem.api.content.type.component;


import com.google.common.base.Preconditions;

public class Occurrences
{
    private int minimum;

    /**
     * Zero means unlimited;
     */
    private int maximum;

    public Occurrences( final int minimum, final int maximum )
    {
        Preconditions.checkArgument( minimum >= 0 );
        Preconditions.checkArgument( maximum >= 0 );

        this.minimum = minimum;
        this.maximum = maximum;
    }

    public void setMinOccurences( final int value )
    {
        this.minimum = value;
    }

    public void setMaxOccurences( final int value )
    {
        this.maximum = value;
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

        final Occurrences occurrences = (Occurrences) o;

        if ( maximum != occurrences.maximum )
        {
            return false;
        }
        if ( minimum != occurrences.minimum )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = minimum;
        result = 31 * result + maximum;
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append( "min=" ).append( minimum ).append( ", max=" ).append( maximum );
        return s.toString();
    }
}
