package com.enonic.wem.core.content.data;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;

public class DataSet
    extends Entry
    implements Iterable<Entry>
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

    boolean isUnstructured()
    {
        return configItems == null;
    }

    void add( Entry entry )
    {
        entries.put( entry.getPath().getLastElement(), entry );
    }

    void setData( final EntryPath path, final Object value )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( isUnstructured() )
        {
            setUnstructuredData( path, value );
        }
        else
        {
            setStructuredData( path, value );
        }
    }

    private void setStructuredData( final EntryPath path, final Object value )
    {
        final ConfigItemPath configItemPath = path.resolveConfigItemPath();
        final ConfigItem foundConfig = configItems.getConfigItem( configItemPath.getFirstElement() );
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
            forwardSetDataToDataSet( path, value, foundFieldSet );
        }
        else
        {
            final Field field = (Field) foundConfig;
            doSetEntry( path.getFirstElement(), Data.newBuilder().field( field ).path( path ).value( value ).build() );
        }
    }

    private void setUnstructuredData( final EntryPath path, final Object value )
    {
        if ( path.elementCount() > 1 )
        {
            forwardSetDataToDataSet( path, value );
        }
        else
        {
            Data newData = Data.newBuilder().path( new EntryPath( this.path, path.getFirstElement() ) ).value( value ).build();
            doSetEntry( path.getFirstElement(), newData );
        }
    }

    private void forwardSetDataToDataSet( final EntryPath path, final Object value )
    {
        DataSet existingDataSet = (DataSet) this.entries.get( path.getFirstElement() );
        if ( existingDataSet == null )
        {
            existingDataSet = new DataSet( new EntryPath( this.path, path.getFirstElement() ) );
            doSetEntry( path.getFirstElement(), existingDataSet );
        }
        existingDataSet.setData( path.asNewWithoutFirstPathElement(), value );
    }

    private void forwardSetDataToDataSet( final EntryPath path, final Object value, final FieldSet fieldSet )
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
        existingDataSet.setData( path.asNewWithoutFirstPathElement(), value );
    }

    private void doSetEntry( EntryPath.Element element, Entry entry )
    {
        entries.put( element, entry );
    }

    Data getData( final String path )
    {
        return getData( new EntryPath( path ) );
    }

    Data getData( final EntryPath path )
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

    DataSet getDataSet( final EntryPath path )
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

    Entry getEntry( String path )
    {
        return getEntry( new EntryPath( path ) );
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

    public void checkBreaksRequiredContract()
    {
        // check existing entries..
        for ( Entry entry : entries.values() )
        {
            entry.checkBreaksRequiredContract();
        }

        // check missing required entries
        for ( ConfigItem configItem : configItems )
        {
            // TODO: check that required configItems have entries
        }
    }

    public boolean breaksRequiredContract()
    {
        for ( Entry entry : entries.values() )
        {
            if ( entry.breaksRequiredContract() )
            {
                return true;
            }
        }
        return false;
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


}
