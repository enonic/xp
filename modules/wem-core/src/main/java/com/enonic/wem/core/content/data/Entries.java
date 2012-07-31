package com.enonic.wem.core.content.data;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldPath;
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
        Preconditions.checkArgument( configItems.getPath().equals( path.resolveFieldPath() ),
                                     "path [%s] does not correspond with configItems.path: " + configItems.getPath(), path.toString() );

        this.path = path;
        this.configItems = configItems;
    }

    public Entries( final EntryPath path, final FieldSet fieldSet )
    {
        Preconditions.checkNotNull( fieldSet, "fieldSet cannot be null" );
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.configItems = fieldSet.getConfigItems();
        this.path = path;
    }

    public Entries( final EntryPath path, final Entries entries )
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
        final FieldPath fieldPath = path.resolveFieldPath();
        final ConfigItem foundConfig = configItems.getConfig( fieldPath.getFirstElement() );
        if ( foundConfig == null )
        {
            throw new IllegalArgumentException( "No ConfigItem found at: " + path );
        }

        if ( path.elementCount() > 1 )
        {
            Preconditions.checkArgument( foundConfig instanceof FieldSet,
                                         "ConfigItem at path [%s] expected to be of type FieldSet: " + foundConfig.getConfigItemType(),
                                         path );

            @SuppressWarnings("ConstantConditions") final FieldSet fieldSet = (FieldSet) foundConfig;
            forwardSetValueToEntries( path, value, fieldSet );
        }
        else
        {
            final Field field = (Field) foundConfig;
            doSetEntry( path.getLastElement(), Value.newBuilder().field( field ).path( path ).value( value ).build() );
        }
    }

    private void setUnstructuredValue( final EntryPath path, final Object value )
    {
        if ( path.elementCount() > 1 )
        {
            final Entry entry = entries.get( path.getFirstElement() );
            final EntryPath newPath = path.asNewWithoutFirstPathElement();
            if ( entry == null )
            {
                final Entries newEntries = new Entries( new EntryPath( this.path, path.getFirstElement() ) );
                entries.put( path.getFirstElement(), newEntries );
                newEntries.setValue( newPath, value );
            }
            else
            {
                final Entries existingEntries = (Entries) entry;
                existingEntries.setValue( newPath, value );
            }

        }
        else
        {
            Value newValue = Value.newBuilder().path( new EntryPath( this.path, path.getFirstElement() ) ).value( value ).build();
            doSetEntry( path.getLastElement(), newValue );
        }
    }

    private void forwardSetValueToEntries( final EntryPath path, final Object value, final FieldSet fieldSet )
    {
        final EntryPath.Element pathFirstElement = path.getFirstElement();
        Entries existingEntries = (Entries) this.entries.get( pathFirstElement );
        if ( existingEntries == null )
        {
            existingEntries = new Entries( new EntryPath( this.path, pathFirstElement ), fieldSet );
            doSetEntry( pathFirstElement, existingEntries );
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

        //Preconditions.checkArgument( foundEntry instanceof Value,
        //                             "Entry [%s] in Entries [%s] expected to be a Value: " + foundEntry.getClass().getName(),
        //                             foundEntry.getName(), this.getPath() );

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


}
