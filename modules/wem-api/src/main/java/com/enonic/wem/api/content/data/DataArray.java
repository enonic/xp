package com.enonic.wem.api.content.data;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public abstract class DataArray
    implements Iterable<Data>
{
    private DataSet parent;

    private String name;

    private final ArrayList<Data> list = new ArrayList<Data>();

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

    public String getName()
    {
        return name;
    }

    void add( final Data data )
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

    void set( final int index, final Data data )
    {
        checkType( data );
        checkParent( data );

        if ( overwritesExisting( index, list ) )
        {
            list.set( index, data );
        }
        else
        {
            checkIndexIsSuccessive( index, data );
            list.add( data );
        }
    }

    public Data getData( final int i )
    {
        if ( i > list.size() - 1 )
        {
            return null;
        }
        return list.get( i );
    }

    public Property getProperty( final int i )
    {
        Data data = getData( i );
        if ( data == null )
        {
            return null;
        }

        return data.toProperty();
    }

    public DataSet getDataSet( final int i )
    {
        Data data = getData( i );
        if ( data == null )
        {
            return null;
        }

        return data.toDataSet();
    }

    public int size()
    {
        return list.size();
    }

    @Override
    public Iterator<Data> iterator()
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


    abstract void checkType( Data data );

    private void checkParent( final Data data )
    {
        if ( !data.getParent().equals( parent ) )
        {
            throw new IllegalArgumentException( "Data added to array [" + getPath() + "] does not have same parent: " + data.getParent() );
        }
    }

    void checkIndexIsSuccessive( final int index, final Data data )
    {
        Preconditions.checkArgument( index == list.size(),
                                     "Data [%s] not added successively to array [%s] with size %s. Data had unexpected index: %s", data,
                                     getPath(), list.size(), index );
    }

    boolean overwritesExisting( final int index, final List list )
    {
        return index < list.size();
    }

    public int getIndex( final Data data )
    {
        for ( int i = 0; i < list.size(); i++ )
        {
            final Data currData = list.get( i );
            if ( data.equals( currData ) )
            {
                return i;
            }
        }
        return -1;
    }

    public List<Data> asList()
    {
        return Lists.newArrayList( list );
    }
}
