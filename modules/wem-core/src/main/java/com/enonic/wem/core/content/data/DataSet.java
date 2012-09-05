package com.enonic.wem.core.content.data;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.datatype.DataType;
import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.DirectAccessibleConfigItem;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;

public class DataSet
    extends Entry
    implements Iterable<Entry>, EntrySelector
{
    private EntryPath path;

    private ConfigItems configItems;

    private LinkedHashMap<EntryPath.Element, Entry> entries = new LinkedHashMap<EntryPath.Element, Entry>();

    public DataSet( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.path = path;
    }

    public DataSet( final EntryPath path, final ConfigItems configItems )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( configItems, "configItems cannot be null" );
        Preconditions.checkArgument( configItems.getPath().equals( path.resolveConfigItemPath() ),
                                     "path [%s] does not correspond with configItems.path: " + configItems.getPath(), path.toString() );

        this.path = path;
        this.configItems = configItems;
    }

    private DataSet( final EntryPath path, final FieldSet fieldSet )
    {
        Preconditions.checkNotNull( fieldSet, "fieldSet cannot be null" );
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.configItems = fieldSet.getConfigItems();
        this.path = path;
    }

    DataSet( final EntryPath path, final DataSet dataSet )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( dataSet, "entries cannot be null" );

        this.path = path;
        this.entries = dataSet.entries;
    }

    public DataSet( final EntryPath path, final FieldSet fieldSet, final DataSet dataSet )
    {
        Preconditions.checkNotNull( fieldSet, "fieldSet cannot be null" );
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( dataSet, "entries cannot be null" );

        this.configItems = fieldSet.getConfigItems();
        this.path = path;
        this.entries = dataSet.entries;
    }

    public void setConfigItems( final ConfigItems configItems )
    {
        Preconditions.checkNotNull( configItems, "configItems cannot be null" );

        ConfigItemPath configItemPath = path.resolveConfigItemPath();
        org.elasticsearch.common.base.Preconditions.checkArgument( configItemPath.equals( configItems.getPath() ),
                                                                   "This DataSet' path [%s] does not match given ConfigItems' path: " +
                                                                       configItems.getPath(), configItemPath.toString() );
        this.configItems = configItems;

        for ( Entry entry : entries.values() )
        {
            if ( entry instanceof Data )
            {
                final Data data = (Data) entry;
                final Field field = configItems.getField( entry.getName() );
                if ( field != null )
                {
                    data.setField( field );
                }
            }
            else if ( entry instanceof DataSet )
            {
                final DataSet dataSet = (DataSet) entry;
                final FieldSet fieldSet = configItems.getFieldSet( entry.getName() );
                if ( fieldSet != null )
                {
                    dataSet.setConfigItems( fieldSet.getConfigItems() );
                }
            }
        }
    }

    public EntryPath getPath()
    {
        return path;
    }

    boolean isUntyped()
    {
        return configItems == null;
    }

    void add( Entry entry )
    {
        entries.put( entry.getPath().getLastElement(), entry );
    }

    void setData( final EntryPath path, final Object value, final DataType dataType )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( isUntyped() )
        {
            setUntypedData( path, value, dataType );
        }
        else
        {
            setTypedData( path, value, dataType );
        }
    }

    private void setTypedData( final EntryPath path, final Object value, DataType dataType )
    {
        final ConfigItemPath configItemPath = path.resolveConfigItemPath();
        final DirectAccessibleConfigItem foundConfig = configItems.getDirectAccessibleConfigItem( configItemPath.getFirstElement() );
        if ( foundConfig == null )
        {
            throw new IllegalArgumentException( "No ConfigItem found at: " + path );
        }

        if ( path.elementCount() > 1 )
        {
            Preconditions.checkArgument( foundConfig instanceof FieldSet,
                                         "ConfigItem at path [%s] expected to be of type FieldSet: " + foundConfig.getConfigItemType(),
                                         path );

            //noinspection ConstantConditions
            FieldSet foundFieldSet = (FieldSet) foundConfig;
            forwardSetDataToDataSet( path, value, dataType, foundFieldSet );
        }
        else
        {
            Preconditions.checkArgument( foundConfig instanceof Field,
                                         "ConfigItem at path [%s] expected to be of type Field: " + foundConfig.getConfigItemType(), path );
            //noinspection ConstantConditions
            final Field field = (Field) foundConfig;
            if ( dataType == null )
            {
                dataType = field.getFieldType().getDataType();
            }
            doSetEntry( path.getFirstElement(), Data.newBuilder().field( field ).path( path ).value( value ).build() );
        }
    }

    private void setUntypedData( final EntryPath path, final Object value, final DataType dataType )
    {
        if ( path.elementCount() > 1 )
        {
            forwardSetDataToDataSet( path, value, dataType );
        }
        else
        {
            final EntryPath newEntryPath = new EntryPath( this.path, path.getFirstElement() );
            final Data newData = Data.newData().path( newEntryPath ).type( dataType ).value( value ).build();
            doSetEntry( path.getFirstElement(), newData );
        }
    }

    private void forwardSetDataToDataSet( final EntryPath path, final Object value, final DataType dataType )
    {
        DataSet existingDataSet = (DataSet) this.entries.get( path.getFirstElement() );
        if ( existingDataSet == null )
        {
            existingDataSet = new DataSet( new EntryPath( this.path, path.getFirstElement() ) );
            doSetEntry( path.getFirstElement(), existingDataSet );
        }
        existingDataSet.setData( path.asNewWithoutFirstPathElement(), value, dataType );
    }

    private void forwardSetDataToDataSet( final EntryPath path, final Object value, final DataType dataType, final FieldSet fieldSet )
    {
        if ( path.getFirstElement().hasPosition() )
        {
            Preconditions.checkArgument( fieldSet.isMultiple(),
                                         "Trying to set an occurrence on a non-multiple FieldSet [%s]: " + path.getFirstElement(),
                                         fieldSet );
        }

        DataSet existingDataSet = (DataSet) this.entries.get( path.getFirstElement() );
        if ( existingDataSet == null )
        {
            existingDataSet = new DataSet( new EntryPath( this.path, path.getFirstElement() ), fieldSet );
            doSetEntry( path.getFirstElement(), existingDataSet );
        }
        existingDataSet.setData( path.asNewWithoutFirstPathElement(), value, dataType );
    }

    private void doSetEntry( EntryPath.Element element, Entry entry )
    {
        entries.put( element, entry );
    }

    public Data getData( final String path )
    {
        return getData( new EntryPath( path ) );
    }


    public Data getData( final EntryPath path )
    {
        final Entry entry = getEntry( path );
        if ( entry == null )
        {
            return null;
        }
        Preconditions.checkArgument( ( entry instanceof Data ), "Entry at path [%s] is not a Data: " + entry, path );
        //noinspection ConstantConditions
        return (Data) entry;
    }

    public DataSet getDataSet( final EntryPath path )
    {
        final Entry entry = getEntry( path );
        if ( entry == null )
        {
            return null;
        }
        Preconditions.checkArgument( ( entry instanceof DataSet ), "Entry at path [%s] is not a DataSet: " + entry, path );
        //noinspection ConstantConditions
        return (DataSet) entry;
    }

    Entry getEntry( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            return forwardGetEntryToDataSet( path );
        }
        else
        {
            return doGetEntry( path );
        }
    }

    private Entry forwardGetEntryToDataSet( final EntryPath path )
    {
        final Entry foundEntry = entries.get( path.getFirstElement() );
        if ( foundEntry == null )
        {
            return null;
        }

        Preconditions.checkArgument( foundEntry instanceof DataSet,
                                     "Entry [%s] in DataSet [%s] expected to be a DataSet: " + foundEntry.getClass().getName(),
                                     foundEntry.getName(), this.getPath() );

        //noinspection ConstantConditions
        return ( (DataSet) foundEntry ).getEntry( path.asNewWithoutFirstPathElement() );
    }

    private Entry doGetEntry( final EntryPath path )
    {
        Preconditions.checkArgument( path.elementCount() == 1, "path expected to contain only one element: " + path );

        final Entry foundEntry = entries.get( path.getLastElement() );
        if ( foundEntry == null )
        {
            return null;
        }

        return foundEntry;
    }

    public Iterator<Entry> iterator()
    {
        return entries.values().iterator();
    }

    public int size()
    {
        return entries.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        s.append( path.toString() );
        s.append( ": " );
        int index = 0;
        final int size = entries.size();
        for ( Entry entry : entries.values() )
        {
            s.append( entry.getPath().getLastElement() );
            if ( index < size - 1 )
            {
                s.append( ", " );
            }
            index++;
        }
        return s.toString();
    }


    public boolean hasDataAtPath( ConfigItemPath path )
    {
        for ( Entry entry : entries.values() )
        {
            if ( entry instanceof Data )
            {
                Data data = (Data) entry;
                if ( data.getField() != null && data.getPath().resolveConfigItemPath().equals( path ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasDataSetAtPath( ConfigItemPath path )
    {
        for ( Entry entry : entries.values() )
        {
            if ( entry instanceof DataSet )
            {
                DataSet dataSet = (DataSet) entry;
                if ( dataSet.configItems.getPath().equals( path ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

}
