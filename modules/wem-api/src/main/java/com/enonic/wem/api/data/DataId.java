package com.enonic.wem.api.data;

import com.google.common.base.Preconditions;

public class DataId
{
    private final String name;

    private final int index;

    private final String refString;

    private DataId( final String name, final int index )
    {
        Preconditions.checkNotNull( name, "name cannot be null" );
        Preconditions.checkArgument( index >= 0, "index must be zero or more" );
        Preconditions.checkArgument( !name.contains( "[" ), "name cannot contain [" );
        Preconditions.checkArgument( !name.contains( "]" ), "name cannot contain ]" );
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

        final DataId dataId = (DataId) o;

        if ( !refString.equals( dataId.refString ) )
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

    public static DataId from( final String name, final int index )
    {
        return new DataId( name, index );
    }

    public static DataId from( final DataPath.Element pathElement )
    {
        return new DataId( pathElement.getName(), pathElement.hasIndex() ? pathElement.getIndex() : 0 );
    }

    public static DataId from( final String dataId )
    {
        if ( !dataId.contains( "[" ) )
        {
            return new DataId( dataId, 0 );
        }

        int bracketStart = dataId.indexOf( "[" );
        int bracketEnd = dataId.indexOf( "]" );
        if ( bracketEnd < bracketStart )
        {
            throw new IllegalArgumentException( "Illegal syntax of DataId. End bracket is before start bracket: " + dataId );
        }
        final String name = dataId.substring( 0, bracketStart );
        final String indexAsString = dataId.substring( bracketStart + 1, bracketEnd );
        final Integer index = Integer.valueOf( indexAsString );
        return new DataId( name, index );
    }
}
