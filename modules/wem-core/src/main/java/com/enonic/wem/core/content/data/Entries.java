package com.enonic.wem.core.content.data;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldPath;
import com.enonic.wem.core.content.type.configitem.SubType;

public class Entries
    implements Iterable<Entry>
{
    private EntryPath path;

    private ConfigItems configItems;

    private LinkedHashMap<EntryPath.Element, Entry> entries = new LinkedHashMap<EntryPath.Element, Entry>();

    /**
     * Structured.
     *
     * @param configItems
     */
    public Entries( final ConfigItems configItems )
    {
        Preconditions.checkNotNull( configItems, "configItems cannot be null" );
        this.path = new EntryPath();
        Preconditions.checkArgument( configItems.getPath().equals( path.resolveFieldPath() ),
                                     "path [%s] does not correspond with configItems.path: " + configItems.getPath(), path.toString() );
        this.configItems = configItems;
    }

    /**
     * Structured.
     *
     * @param configItems
     */
    public Entries( final EntryPath path, final ConfigItems configItems )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( configItems, "configItems cannot be null" );
        Preconditions.checkArgument( configItems.getPath().equals( path.resolveFieldPath() ),
                                     "path [%s] does not correspond with configItems.path: " + configItems.getPath(), path.toString() );

        this.path = path;
        this.configItems = configItems;
    }

    /**
     * Unstructured.
     */
    public Entries( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        this.path = path;
    }

    public EntryPath getPath()
    {
        return path;
    }

    public boolean isUnstructured()
    {
        return configItems == null;
    }

    void add( Entry entry )
    {
        entries.put( entry.getPath().getLastElement(), entry );
    }

    public void setValue( final EntryPath path, final Object value )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( isUnstructured() )
        {
            if ( path.elementCount() > 1 )
            {
                Entry entry = entries.get( path.getFirstElement() );
                EntryPath newPath = path.asNewWithoutFirstPathElement();
                if ( entry == null )
                {
                    SubTypeEntry subTypeEntry = new SubTypeEntry( new EntryPath( this.path, path.getFirstElement() ) );
                    entries.put( path.getFirstElement(), subTypeEntry );
                    subTypeEntry.setValue( newPath, value );
                }
                else
                {
                    SubTypeEntry subTypeEntry = (SubTypeEntry) entry;
                    subTypeEntry.setValue( newPath, value );
                }

            }
            else
            {
                Value newValue = Value.newBuilder().path( new EntryPath( this.path, path.getFirstElement() ) ).value( value ).build();
                doSetEntry( path.getLastElement(), newValue );
            }
        }
        else
        {
            final FieldPath fieldPath = path.resolveFieldPath();
            ConfigItem foundConfig = configItems.getConfig( fieldPath.getFirstElement() );
            if ( foundConfig == null )
            {
                throw new IllegalArgumentException( "No ConfigItem found at: " + path );
            }

            if ( path.elementCount() > 1 )
            {
                Preconditions.checkArgument( foundConfig instanceof SubType,
                                             "ConfigItem at path [%s] expected to be of type SubType: " + foundConfig.getItemType(), path );

                @SuppressWarnings("ConstantConditions") final SubType subType = (SubType) foundConfig;
                forwardSetValueToSubTypeEntry( path, value, subType );
            }
            else
            {
                final Field field = (Field) foundConfig;
                doSetEntry( path.getLastElement(), Value.newBuilder().field( field ).path( path ).value( value ).build() );
            }
        }
    }

    private void forwardSetValueToSubTypeEntry( final EntryPath path, final Object value, final SubType subType )
    {
        final EntryPath.Element pathFirstElement = path.getFirstElement();
        SubTypeEntry subTypeEntry = (SubTypeEntry) entries.get( pathFirstElement );
        if ( subTypeEntry == null )
        {
            subTypeEntry = new SubTypeEntry( subType, new EntryPath( this.path, pathFirstElement ) );
            doSetEntry( pathFirstElement, subTypeEntry );
        }
        subTypeEntry.setValue( path.asNewWithoutFirstPathElement(), value );
    }

    private void doSetEntry( EntryPath.Element element, Entry entry )
    {
        entries.put( element, entry );
    }

    public Value getValue( final String path )
    {
        return getValue( new EntryPath( path ) );
    }

    public Value getValue( final EntryPath path )
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

    public Entry getEntry( String path )
    {
        return getEntry( new EntryPath( path ) );
    }

    public Entry getEntry( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkArgument( path.elementCount() >= 1, "path must be something: " + path );

        if ( path.elementCount() > 1 )
        {
            return forwardGetEntryToSubTypeEntry( path );
        }
        else
        {
            return doGetEntry( path );
        }
    }

    private Entry forwardGetEntryToSubTypeEntry( final EntryPath path )
    {
        final Entry foundEntry = entries.get( path.getFirstElement() );
        if ( foundEntry == null )
        {
            return null;
        }

        Preconditions.checkArgument( foundEntry instanceof SubTypeEntry,
                                     "Entry [%s] in Entries [$s] expected to be a SubType: " + foundEntry.getClass().getName(),
                                     foundEntry.getName(), this.getPath() );

        //noinspection ConstantConditions
        return ( (SubTypeEntry) foundEntry ).getEntry( path.asNewWithoutFirstPathElement() );
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
