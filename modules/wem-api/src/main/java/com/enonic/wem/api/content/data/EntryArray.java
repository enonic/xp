package com.enonic.wem.api.content.data;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public abstract class EntryArray
    implements Iterable<Entry>
{
    private DataSet parent;

    private String name;

    private final ArrayList<Entry> list = new ArrayList<Entry>();

    EntryArray( final DataSet parent, final String name )
    {
        Preconditions.checkNotNull( name, "name cannot be null" );
        this.parent = parent;
        this.name = name;
    }

    public EntryPath getPath()
    {
        if ( parent != null )
        {
            return EntryPath.from( parent.getPath(), name );
        }
        else
        {
            return EntryPath.from( name );
        }
    }

    public String getName()
    {
        return name;
    }

    void add( final Entry entry )
    {
        checkType( entry );
        checkParent( entry );

        final EntryPath entryPath = entry.getPath();
        if ( entryPath.getLastElement().hasIndex() )
        {
            final int index = entryPath.getLastElement().getIndex();
            checkIndexIsSuccessive( index, entry );
        }

        list.add( entry );

        for ( Entry currEntry : list )
        {
            currEntry.invalidatePath();
        }
    }

    void set( final int index, final Entry entry )
    {
        checkType( entry );
        checkParent( entry );

        if ( overwritesExisting( index, list ) )
        {
            list.set( index, entry );
        }
        else
        {
            checkIndexIsSuccessive( index, entry );
            list.add( entry );
        }
    }

    public Entry getEntry( final int i )
    {
        if ( i > list.size() - 1 )
        {
            return null;
        }
        return list.get( i );
    }

    public Data getData( final int i )
    {
        Entry entry = getEntry( i );
        if ( entry == null )
        {
            return null;
        }

        return entry.toData();
    }

    public DataSet getDataSet( final int i )
    {
        Entry entry = getEntry( i );
        if ( entry == null )
        {
            return null;
        }

        return entry.toDataSet();
    }

    public int size()
    {
        return list.size();
    }

    @Override
    public Iterator<Entry> iterator()
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
            final Entry entry = list.get( i );
            s.append( entry );
            if ( i < list.size() - 1 )
            {
                s.append( ", " );
            }
        }
        s.append( " ]" );
        return s.toString();
    }


    abstract void checkType( Entry entry );

    private void checkParent( final Entry entry )
    {
        if ( !entry.getParent().equals( parent ) )
        {
            throw new IllegalArgumentException(
                "Entry added to array [" + getPath() + "] does not have same parent: " + entry.getParent() );
        }
    }

    void checkIndexIsSuccessive( final int index, final Entry entry )
    {
        Preconditions.checkArgument( index == list.size(),
                                     "Entry [%s] not added successively to array [%s] with size %s. Entry had unexpected index: %s", entry,
                                     getPath(), list.size(), index );
    }

    boolean overwritesExisting( final int index, final List list )
    {
        return index < list.size();
    }

    public int getIndex( final Entry entry )
    {
        for ( int i = 0; i < list.size(); i++ )
        {
            final Entry currEntry = list.get( i );
            if ( entry.equals( currEntry ) )
            {
                return i;
            }
        }
        return -1;
    }

    public List<Entry> asList()
    {
        return Lists.newArrayList( list );
    }
}
