package com.enonic.wem.api.content.data;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public abstract class DataArray<T extends Data>
    implements Iterable<T>
{
    private DataSet parent;

    private String name;

    private final ArrayList<T> list = new ArrayList<>();

    DataArray( final DataSet parent, final String name )
    {
        Preconditions.checkNotNull( name, "name cannot be null" );
        this.parent = parent;
        this.name = name;
    }

    public DataPath getPath()
    {
        if ( parent != null )
        {
            return DataPath.from( parent.getPath(), name );
        }
        else
        {
            return DataPath.from( name );
        }
    }

    void add( final T data )
    {
        checkType( data );
        checkParent( data );

        final DataPath dataPath = data.getPath();
        if ( dataPath.getLastElement().hasIndex() )
        {
            final int index = dataPath.getLastElement().getIndex();
            checkIndexIsSuccessive( index, data );
        }

        list.add( data );

        for ( Data currData : list )
        {
            currData.invalidatePath();
        }
    }

    abstract void checkType( T data );

    private void checkParent( final Data data )
    {
        if ( !data.getParent().equals( parent ) )
        {
            throw new IllegalArgumentException( "Data added to array [" + getPath() + "] does not have same parent: " + data.getParent() );
        }
    }

    public T getData( final int i )
    {
        if ( i > list.size() - 1 )
        {
            return null;
        }
        return list.get( i );
    }

    public int size()
    {
        return list.size();
    }

    @Override
    public Iterator<T> iterator()
    {
        return list.iterator();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        s.append( getPath() );
        s.append( " [ " );
        for ( int i = 0; i < list.size(); i++ )
        {
            final Data data = list.get( i );
            s.append( data );
            if ( i < list.size() - 1 )
            {
                s.append( ", " );
            }
        }
        s.append( " ]" );
        return s.toString();
    }

    private void checkIndexIsSuccessive( final int index, final Data data )
    {
        Preconditions.checkArgument( index == list.size(),
                                     "Data [%s] not added successively to array [%s] with size %s. Data had unexpected index: %s", data,
                                     getPath(), list.size(), index );
    }

    public List<T> asList()
    {
        return Lists.newArrayList( list );
    }
}
