package com.enonic.wem.api.content.data;

import com.google.common.base.Preconditions;

public class EntryId
{
    private final String name;

    private final int index;

    private final String refString;

    private EntryId( final String name, final int index )
    {
        Preconditions.checkNotNull( name, "name cannot be null" );
        Preconditions.checkArgument( index >= 0, "index must be zero or more" );
        this.name = name;
        this.index = index;
        this.refString = toString( name, index );
    }

    public String getName()
    {
        return name;
    }

    public int getIndex()
    {
        return index;
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

        final EntryId entryId = (EntryId) o;

        if ( !refString.equals( entryId.refString ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }

    private String toString( final String name, final int index )
    {
        StringBuilder s = new StringBuilder();
        s.append( name );
        if ( index > 0 )
        {
            s.append( "[" ).append( index ).append( "]" );
        }
        return s.toString();
    }

    public static EntryId from( final String name, final int index )
    {
        return new EntryId( name, index );
    }

    public static EntryId from( final EntryPath.Element pathElement )
    {
        return new EntryId( pathElement.getName(), pathElement.hasIndex() ? pathElement.getIndex() : 0 );
    }
}
