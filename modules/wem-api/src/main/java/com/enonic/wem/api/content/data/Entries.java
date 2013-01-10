package com.enonic.wem.api.content.data;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.type.form.InvalidDataException;

import static com.enonic.wem.api.content.data.Data.newData;
import static com.enonic.wem.api.content.data.DataSet.newDataSet;

final class Entries
    implements Iterable<Entry>
{
    private DataSet dataSet;

    private LinkedHashMap<EntryId, Entry> entryById = new LinkedHashMap<EntryId, Entry>();

    private Map<String, EntryArray> arrayByEntryName = new HashMap<String, EntryArray>();

    Entries( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    final int getArrayIndex( Entry entry )
    {
        final EntryArray array = arrayByEntryName.get( entry.getName() );
        if ( array == null )
        {
            return -1;
        }
        return array.getIndex( entry );
    }

    final EntryPath getPath()
    {
        return dataSet.getPath();
    }

    final DataSet getDataSet()
    {
        return dataSet;
    }

    final void add( final Entry newEntry )
    {
        doAdd( newEntry );
    }

    final void setData( final EntryPath path, final Value value )
        throws InvalidDataException
    {

        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            forwardSetDataToDataSet( path, value );
        }
        else
        {
            final EntryPath newPath = EntryPath.from( dataSet.getPath(), path.getFirstElement() );
            setData( EntryId.from( newPath.getLastElement() ), value );
        }
    }

    final Value getValue( final EntryPath path )
    {
        final Entry entry = getEntry( path );
        if ( entry == null )
        {
            return null;
        }

        Preconditions.checkArgument( entry.isData(), "Entry at path[%s] is not a Data: %s", path, entry.getClass().getSimpleName() );
        final Data data = entry.toData();
        if ( path.getLastElement().hasIndex() )
        {
            return data.getValue( path.getLastElement().getIndex() );
        }
        else
        {
            return data.getValue( 0 );
        }
    }

    final DataSet getDataSet( final EntryPath path )
    {
        Preconditions.checkArgument( path.elementCount() > 0, "path must be something: " + path );

        if ( path.elementCount() == 1 )
        {
            final Entry entry = entryById.get( EntryId.from( path.getLastElement() ) );
            if ( entry == null )
            {
                return null;
            }
            return entry.toDataSet();
        }
        else
        {
            final Entry entry = entryById.get( EntryId.from( path.getFirstElement() ) );
            final DataSet dataSet = entry.toDataSet();
            return dataSet.getDataSet( path.asNewWithoutFirstPathElement() );
        }
    }

    final Entry getEntry( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            return forwardGetEntryToDataSet( path );
        }
        else
        {
            return doGetEntry( EntryId.from( path.getLastElement() ) );
        }
    }

    final Entry doGetEntry( final EntryId entryId )
    {
        final Entry entry = entryById.get( entryId );
        if ( entry == null )
        {
            return null;
        }

        return entry;
    }

    final int size()
    {
        return entryById.size();
    }

    final public Iterator<Entry> iterator()
    {
        return entryById.values().iterator();
    }

    final boolean isArray( final Entry entry )
    {
        return arrayByEntryName.get( entry.getName() ).size() > 1;
    }

    final EntryArray getArray( final Entry entry )
    {
        return getArray( entry.getName() );
    }

    final EntryArray getArray( final String entryName )
    {
        return arrayByEntryName.get( entryName );
    }

    final Iterable<String> entryNames()
    {
        return arrayByEntryName.keySet();
    }

    final int entryCount( final String entryName )
    {
        EntryPath.Element.checkName( entryName );
        EntryArray array = arrayByEntryName.get( entryName );
        if ( array == null )
        {
            return 0;
        }
        return array.size();
    }

    public List<Entry> entries( final String name )
    {
        EntryPath.Element.checkName( name );
        EntryArray array = arrayByEntryName.get( name );
        return array.asList();
    }

    public List<DataSet> dataSets( final String name )
    {
        EntryPath.Element.checkName( name );

        final EntryArray array = arrayByEntryName.get( name );

        if ( array == null )
        {
            return Lists.newArrayList();
        }
        else if ( array instanceof DataSetArray )
        {
            final List<DataSet> list = Lists.newArrayList();
            final DataSetArray dataSetArray = (DataSetArray) array;
            for ( Entry entry : dataSetArray )
            {
                list.add( entry.toDataSet() );
            }
            return list;
        }
        else
        {
            throw new IllegalArgumentException( "Entry with name [" + name + "] in [" + getPath() + "] is not a DataSet" );
        }
    }

    @Override
    final public String toString()
    {
        final StringBuilder s = new StringBuilder();
        s.append( dataSet.getName() );
        if ( s.length() > 0 )
        {
            s.append( " " );
        }
        s.append( "{" );
        int count = 0;
        for ( Entry entry : this )
        {
            count++;
            if ( count < entryById.size() )
            {
                s.append( entry.getName() ).append( ", " );
            }
        }
        s.append( "}" );
        return s.toString();
    }

    private void registerArray( final Entry newEntry )
    {
        EntryArray array = arrayByEntryName.get( newEntry.getName() );
        if ( array == null )
        {
            if ( newEntry.isData() )
            {
                final Data newData = newEntry.toData();
                array = DataArray.newDataArray().name( newEntry.getName() ).dataType( newData.getType() ).parent( dataSet ).build();
            }
            else
            {
                array = DataSetArray.newDataSetArray().name( newEntry.getName() ).parent( dataSet ).build();
            }
            arrayByEntryName.put( newEntry.getName(), array );
        }

        array.add( newEntry );
    }

    private void setData( final EntryId entryId, final Value value )
    {
        final Entry exEntry = entryById.get( entryId );

        if ( exEntry == null )
        {
            final Data newData = newData().name( entryId.getName() ).value( value ).parent( dataSet ).build();
            registerArray( newData );
            entryById.put( entryId, newData );
        }
        else
        {
            exEntry.toData().setValue( value );
            EntryArray array = arrayByEntryName.get( exEntry.getName() );
            array.set( exEntry.getArrayIndex(), exEntry );
        }
    }

    private void forwardSetDataToDataSet( final EntryPath path, final Value value )
    {
        final DataSet dataSet = findOrCreateDataSet( EntryId.from( path.getFirstElement() ) );
        dataSet.setData( path.asNewWithoutFirstPathElement(), value );
    }

    private Entry forwardGetEntryToDataSet( final EntryPath path )
    {
        final Entry entry = entryById.get( EntryId.from( path.getFirstElement() ) );
        if ( entry == null )
        {
            return null;
        }

        return entry.toDataSet().getEntry( path.asNewWithoutFirstPathElement() );
    }

    private DataSet findOrCreateDataSet( final EntryId entryId )
    {
        final Entry exEntry = entryById.get( entryId );
        if ( exEntry == null )
        {
            final DataSet dataSet = newDataSet().name( entryId.getName() ).parent( this.dataSet ).build();
            doAdd( dataSet );
            return dataSet;
        }
        else
        {
            return exEntry.toDataSet();
        }
    }

    private void doAdd( final Entry newEntry )
    {
        newEntry.setParentEntries( this );
        registerArray( newEntry );
        entryById.put( newEntry.getEntryId(), newEntry );
    }
}
