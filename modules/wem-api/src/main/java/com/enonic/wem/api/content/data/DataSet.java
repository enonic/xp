package com.enonic.wem.api.content.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.datatype.BaseDataType;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.schema.type.form.InvalidDataException;

import static com.enonic.wem.api.content.data.Data.newData;
import static com.enonic.wem.api.content.data.Value.newValue;

public class DataSet
    extends Entry
    implements Iterable<Entry>
{
    private LinkedHashMap<EntryId, Entry> entryById = new LinkedHashMap<>();

    private Map<String, EntryArray> arrayByEntryName = new HashMap<>();

    DataSet()
    {
        // Creates a root DataSet
    }

    private DataSet( final Builder builder )
    {
        super( builder.name );

        for ( Data data : builder.dataList )
        {
            add( data );
        }
    }

    public boolean isRoot()
    {
        return false;
    }

    /**
     * Adds the given Entry to this DataSet. If entries with same name already exists, then it will be positioned last in the array.
     */
    public final void add( final Entry entry )
    {
        doAdd( entry );
    }

    private void doAdd( final Entry newEntry )
    {
        newEntry.setParent( this );
        registerArray( newEntry );
        entryById.put( newEntry.getEntryId(), newEntry );
    }

    public final void setData( final EntryPath path, final String value )
    {
        setData( path, newValue().type( DataTypes.TEXT ).value( value ).build() );
    }

    public final void setData( final EntryPath path, final Value value )
        throws InvalidDataException
    {
        if ( path.elementCount() > 1 )
        {
            forwardSetDataToDataSet( path, value );
        }
        else
        {
            final EntryPath newPath = EntryPath.from( getPath(), path.getFirstElement() );
            setData( EntryId.from( newPath.getLastElement() ), value );
        }
    }

    final void setData( final EntryPath path, final Object valueObject, final BaseDataType dataType )
        throws InvalidDataException
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        final Value value = newValue().type( dataType ).value( valueObject ).build();

        setData( path, value );
    }

    private void setData( final EntryId entryId, final Value value )
    {
        final Entry exEntry = entryById.get( entryId );

        if ( exEntry == null )
        {
            final Data newData = newData().name( entryId.getName() ).value( value ).build();
            newData.setParent( this );
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

    private DataSet findOrCreateDataSet( final EntryId entryId )
    {
        final Entry exEntry = entryById.get( entryId );
        if ( exEntry == null )
        {
            final DataSet dataSet = newDataSet().name( entryId.getName() ).build();
            doAdd( dataSet );
            return dataSet;
        }
        else
        {
            return exEntry.toDataSet();
        }
    }

    private void registerArray( final Entry newEntry )
    {
        EntryArray array = arrayByEntryName.get( newEntry.getName() );
        if ( array == null )
        {
            if ( newEntry.isData() )
            {
                final Data newData = newEntry.toData();
                array = DataArray.newDataArray().name( newEntry.getName() ).dataType( newData.getType() ).parent( this ).build();
            }
            else
            {
                array = DataSetArray.newDataSetArray().name( newEntry.getName() ).parent( this ).build();
            }
            arrayByEntryName.put( newEntry.getName(), array );
        }

        array.add( newEntry );
    }

    public final int size()
    {
        return entryById.size();
    }

    public final Iterator<Entry> iterator()
    {
        return entryById.values().iterator();
    }

    public final Iterable<String> entryNames()
    {
        return arrayByEntryName.keySet();
    }

    public final int entryCount( final String entryName )
    {
        EntryPath.Element.checkName( entryName );
        EntryArray array = arrayByEntryName.get( entryName );
        if ( array == null )
        {
            return 0;
        }
        return array.size();
    }

    public final Entry getEntry( final String path )
    {
        return getEntry( EntryPath.from( path ) );
    }

    public final Entry getEntry( final EntryPath path )
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

    private Entry forwardGetEntryToDataSet( final EntryPath path )
    {
        final Entry entry = entryById.get( EntryId.from( path.getFirstElement() ) );
        if ( entry == null )
        {
            return null;
        }

        return entry.toDataSet().getEntry( path.asNewWithoutFirstPathElement() );
    }

    private final Entry doGetEntry( final EntryId entryId )
    {
        final Entry entry = entryById.get( entryId );
        if ( entry == null )
        {
            return null;
        }

        return entry;
    }

    public final List<Entry> entries( final String entryName )
    {
        EntryPath.Element.checkName( entryName );
        EntryArray array = arrayByEntryName.get( entryName );
        return array.asList();
    }

    public final Data getData( final String path )
    {
        return getData( EntryPath.from( path ) );
    }

    public final Data getData( final EntryPath path )
    {
        Entry entry = getEntry( path );
        if ( entry == null )
        {
            return null;
        }
        Preconditions.checkArgument( entry.isData(), "Entry at path [%s] is not a Data: %s", path, entry.getClass().getSimpleName() );
        return entry.toData();
    }

    public final Data getData( final String name, final int arrayIndex )
    {
        EntryPath.Element.checkName( name );

        final Entry entry = doGetEntry( EntryId.from( name, arrayIndex ) );
        if ( entry == null )
        {
            return null;
        }
        return entry.toData();
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

    final Value getValue( final String path )
    {
        return getValue( EntryPath.from( path ) );
    }

    public final DataSet getDataSet( final String path )
    {
        final Entry entry = getEntry( EntryPath.from( path ) );
        if ( entry == null )
        {
            return null;
        }
        Preconditions.checkArgument( entry.isDataSet(), "Entry at path[%s] is not a DataSet: %s", path, entry.getClass().getSimpleName() );
        return entry.toDataSet();
    }

    public final DataSet getDataSet( final EntryPath path )
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

    public final DataSet getDataSet( final String name, final int arrayIndex )
    {
        EntryPath.Element.checkName( name );

        final Entry entry = doGetEntry( EntryId.from( name, arrayIndex ) );
        if ( entry == null )
        {
            return null;
        }
        return entry.toDataSet();
    }

    /**
     * Returns all DataSet's with the given name.
     */
    public final List<DataSet> dataSets( final String name )
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

    public final boolean isArray( final Entry entry )
    {
        final EntryArray entryArray = arrayByEntryName.get( entry.getName() );
        return entryArray.size() > 1;
    }

    public final int getArrayIndex( final Entry entry )
    {
        final EntryArray entryArray = arrayByEntryName.get( entry.getName() );
        if ( entryArray == null )
        {
            return -1;
        }
        return entryArray.getIndex( entry );
    }

    public final EntryArray getArray( final Entry entry )
    {
        return arrayByEntryName.get( entry.getName() );
    }

    @Override
    public final DataSetArray getArray()
    {
        return (DataSetArray) super.getArray();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        if ( !StringUtils.isEmpty( getName() ) )
        {
            s.append( getName() );
        }
        if ( getArrayIndex() > -1 )
        {
            s.append( "[" ).append( getArrayIndex() ).append( "]" );
        }
        if ( s.length() > 0 )
        {
            s.append( " " );
        }
        s.append( "{ " );
        int index = 0;
        final int size = size();
        for ( Entry entry : this )
        {
            s.append( entry.getEntryId() );
            if ( index < size - 1 )
            {
                s.append( ", " );
            }
            index++;
        }
        s.append( " }" );
        return s.toString();
    }

    public static Builder newDataSet()
    {
        return new Builder();
    }

    // TODO: Remove and make RootDataSet constructor public instead
    public static RootDataSet newRootDataSet()
    {
        return new RootDataSet();
    }

    public static class Builder
    {
        private String name;

        private List<Data> dataList = new ArrayList<>();

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder set( final String name, final Object value, final BaseDataType dataType )
        {
            dataList.add( newData().name( name ).value( value ).type( dataType ).build() );
            return this;
        }

        public DataSet build()
        {
            return new DataSet( this );
        }
    }
}
