package com.enonic.wem.api.content.data;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.datatype.BaseDataType;
import com.enonic.wem.api.content.type.form.InvalidDataException;

import static com.enonic.wem.api.content.data.Data.newData;

public final class DataSet
    extends Entry
    implements Iterable<Entry>
{
    private Entries entries;

    private DataSet( final String name )
    {
        super( name );
        this.entries = new Entries( this );
    }

    public DataSet( final Builder builder )
    {
        super( builder.name, builder.parent != null ? builder.parent.getEntries() : null );
        this.entries = new Entries( this );

        for ( Data data : builder.dataList )
        {
            add( data );
        }
    }

    Entries getEntries()
    {
        return entries;
    }

    public void add( final Entry entry )
    {
        entries.add( entry );
    }

    void setData( final EntryPath path, final Object value, final BaseDataType dataType )
        throws InvalidDataException
    {
        entries.setData( path, Value.newValue().type( dataType ).value( value ).build() );
    }

    void setData( final EntryPath path, Value value )
        throws InvalidDataException
    {
        entries.setData( path, value );
    }

    public Entry getEntry( final String path )
    {
        return getEntry( EntryPath.from( path ) );
    }

    public Entry getEntry( final EntryPath path )
    {
        return entries.getEntry( path );
    }

    public DataSet getDataSet( final EntryPath path )
    {
        return entries.getDataSet( path );
    }

    public Data getData( final String path )
    {
        return getData( EntryPath.from( path ) );
    }

    public Data getData( final String name, final int arrayIndex )
    {
        EntryPath.Element.checkName( name );

        final Entry entry = entries.doGetEntry( EntryId.from( name, arrayIndex ) );
        if ( entry == null )
        {
            return null;
        }
        return entry.toData();
    }

    public DataSet getDataSet( final String path )
    {
        final Entry entry = getEntry( EntryPath.from( path ) );
        if ( entry == null )
        {
            return null;
        }
        Preconditions.checkArgument( entry.isDataSet(), "Entry at path[%s] is not a DataSet: %s", path, entry.getClass().getSimpleName() );
        return entry.toDataSet();
    }

    public DataSet getDataSet( final String name, final int arrayIndex )
    {
        EntryPath.Element.checkName( name );

        final Entry entry = entries.doGetEntry( EntryId.from( name, arrayIndex ) );
        if ( entry == null )
        {
            return null;
        }
        return entry.toDataSet();
    }

    public Data getData( final EntryPath path )
    {
        Entry entry = getEntry( path );
        if ( entry == null )
        {
            return null;
        }
        Preconditions.checkArgument( entry.isData(), "Entry at path [%s] is not a Data: %s", path, entry.getClass().getSimpleName() );
        return entry.toData();
    }

    Value getValue( final EntryPath path )
    {
        return entries.getValue( path );
    }

    Value getValue( final String path )
    {
        return entries.getValue( EntryPath.from( path ) );
    }

    public Iterator<Entry> iterator()
    {
        return entries.iterator();
    }

    public int size()
    {
        return entries.size();
    }

    public Iterable<String> entryNames()
    {
        return entries.entryNames();
    }

    public int entryCount( final String entryName )
    {
        return entries.entryCount( entryName );
    }

    public List<Entry> entries( final String entryName )
    {
        return entries.entries( entryName );
    }

    public List<DataSet> dataSets( final String name )
    {
        return entries.dataSets( name );
    }

    @Override
    public DataSetArray getArray()
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
        final int size = entries.size();
        for ( Entry entry : entries )
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

    public static DataSet newRootDataSet()
    {
        return new DataSet( "" );
    }

    public static class Builder
    {
        private String name;

        private DataSet parent;

        private List<Data> dataList = new ArrayList<Data>();

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder parent( final DataSet parent )
        {
            this.parent = parent;
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
