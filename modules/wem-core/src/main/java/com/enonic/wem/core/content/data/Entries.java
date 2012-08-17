package com.enonic.wem.core.content.data;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;

public class Entries
    extends Entry
    implements Iterable<Entry>
{
    private EntryPath path;

    private ConfigItems configItems;

    private LinkedHashMap<EntryPath.Element, Entry> entries = new LinkedHashMap<EntryPath.Element, Entry>();

    public Entries( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.path = path;
    }

    public Entries( final EntryPath path, final ConfigItems configItems )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( configItems, "configItems cannot be null" );
        Preconditions.checkArgument( configItems.getPath().equals( path.resolveConfigItemPath() ),
                                     "path [%s] does not correspond with configItems.path: " + configItems.getPath(), path.toString() );

        this.path = path;
        this.configItems = configItems;
    }

    private Entries( final EntryPath path, final FieldSet fieldSet )
    {
        Preconditions.checkNotNull( fieldSet, "fieldSet cannot be null" );
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.configItems = fieldSet.getConfigItems();
        this.path = path;
    }

    Entries( final EntryPath path, final Entries entries )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( entries, "entries cannot be null" );

        this.path = path;
        this.entries = entries.entries;
    }

    public Entries( final EntryPath path, final FieldSet fieldSet, final Entries entries )
    {
        Preconditions.checkNotNull( fieldSet, "fieldSet cannot be null" );
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( entries, "entries cannot be null" );

        this.configItems = fieldSet.getConfigItems();
        this.path = path;
        this.entries = entries.entries;
    }

    public void setConfigItems( final ConfigItems configItems )
    {
        Preconditions.checkNotNull( configItems, "configItems cannot be null" );

        ConfigItemPath configItemPath = path.resolveConfigItemPath();
        org.elasticsearch.common.base.Preconditions.checkArgument( configItemPath.equals( configItems.getPath() ),
                                                                   "This Entries' path [%s] does not match given ConfigItems' path: " +
                                                                       configItems.getPath(), configItemPath.toString() );
        this.configItems = configItems;

        for ( Entry entry : entries.values() )
        {
            if ( entry instanceof Value )
            {
                final Value value = (Value) entry;
                final Field field = configItems.getField( entry.getName() );
                if ( field != null )
                {
                    value.setField( field );
                }
            }
            else if ( entry instanceof Entries )
            {
                final Entries entries = (Entries) entry;
                final FieldSet fieldSet = configItems.getFieldSet( entry.getName() );
                if ( fieldSet != null )
                {
                    entries.setConfigItems( fieldSet.getConfigItems() );
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

    void setValue( final EntryPath path, final Object value )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( isUnstructured() )
        {
            setUnstructuredValue( path, value );
        }
        else
        {
            setStructuredValue( path, value );
        }
    }

    private void setStructuredValue( final EntryPath path, final Object value )
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
            forwardSetValueToEntries( path, value, foundFieldSet );
        }
        else
        {
            final Field field = (Field) foundConfig;
            doSetEntry( path.getFirstElement(), Value.newBuilder().field( field ).path( path ).value( value ).build() );
        }
    }

    private void setUnstructuredValue( final EntryPath path, final Object value )
    {
        if ( path.elementCount() > 1 )
        {
            forwardSetValueToEntries( path, value );
        }
        else
        {
            Value newValue = Value.newBuilder().path( new EntryPath( this.path, path.getFirstElement() ) ).value( value ).build();
            doSetEntry( path.getFirstElement(), newValue );
        }
    }

    private void forwardSetValueToEntries( final EntryPath path, final Object value )
    {
        Entries existingEntries = (Entries) this.entries.get( path.getFirstElement() );
        if ( existingEntries == null )
        {
            existingEntries = new Entries( new EntryPath( this.path, path.getFirstElement() ) );
            doSetEntry( path.getFirstElement(), existingEntries );
        }
        existingEntries.setValue( path.asNewWithoutFirstPathElement(), value );
    }

    private void forwardSetValueToEntries( final EntryPath path, final Object value, final FieldSet fieldSet )
    {
        if ( path.getFirstElement().hasPosition() )
        {
            Preconditions.checkArgument( fieldSet.isMultiple(),
                                         "Trying to set an occurrence on a non-multiple FieldSet [%s]: " + path.getFirstElement(),
                                         fieldSet );
        }

        Entries existingEntries = (Entries) this.entries.get( path.getFirstElement() );
        if ( existingEntries == null )
        {
            existingEntries = new Entries( new EntryPath( this.path, path.getFirstElement() ), fieldSet );
            doSetEntry( path.getFirstElement(), existingEntries );
        }
        existingEntries.setValue( path.asNewWithoutFirstPathElement(), value );
    }

    private void doSetEntry( EntryPath.Element element, Entry entry )
    {
        entries.put( element, entry );
    }

    Value getValue( final String path )
    {
        return getValue( new EntryPath( path ) );
    }

    Value getValue( final EntryPath path )
    {
        final Entry entry = getEntry( path );
        if ( entry == null )
        {
            return null;
        }
        Preconditions.checkArgument( ( entry instanceof Value ), "Entry at path [%s] is not a Value: " + entry, path );
        //noinspection ConstantConditions
        return (Value) entry;
    }

    Entries getEntries( final EntryPath path )
    {
        final Entry entry = getEntry( path );
        if ( entry == null )
        {
            return null;
        }
        Preconditions.checkArgument( ( entry instanceof Entries ), "Entry at path [%s] is not a Entries: " + entry, path );
        //noinspection ConstantConditions
        return (Entries) entry;
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
            return forwardGetEntryToEntries( path );
        }
        else
        {
            return doGetEntry( path );
        }
    }

    private Entry forwardGetEntryToEntries( final EntryPath path )
    {
        final Entry foundEntry = entries.get( path.getFirstElement() );
        if ( foundEntry == null )
        {
            return null;
        }

        Preconditions.checkArgument( foundEntry instanceof Entries,
                                     "Entry [%s] in Entries [%s] expected to be a Entries: " + foundEntry.getClass().getName(),
                                     foundEntry.getName(), this.getPath() );

        //noinspection ConstantConditions
        return ( (Entries) foundEntry ).getEntry( path.asNewWithoutFirstPathElement() );
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
