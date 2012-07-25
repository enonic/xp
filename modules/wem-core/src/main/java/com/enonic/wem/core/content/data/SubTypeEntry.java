package com.enonic.wem.core.content.data;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.SubType;

/**
 * TOOD: Mpve code from Entries into SubTypEntry when we know that ContentData can use SubTypeEntry as container instead of Entries.
 */
public class SubTypeEntry
    extends Entry
{
    private EntryPath path;

    private Entries entries;

    private SubType subType;

    public SubTypeEntry( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.path = path;
        this.entries = new Entries( path );
    }

    public SubTypeEntry( final SubType subType, final EntryPath path )
    {
        Preconditions.checkNotNull( subType, "subType cannot be null" );
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.subType = subType;
        this.path = path;
        this.entries = new Entries( path, subType.getConfigItems() );
    }

    public SubTypeEntry( final SubType subType, final EntryPath path, final Entries entries )
    {
        Preconditions.checkNotNull( subType, "subType cannot be null" );
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( entries, "entries cannot be null" );

        this.subType = subType;
        this.path = path;
        this.entries = entries;
    }

    public SubTypeEntry( final EntryPath path, final Entries entries )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( entries, "entries cannot be null" );

        this.path = path;
        this.entries = entries;
    }

    @Override
    public EntryPath getPath()
    {
        return path;
    }

    public Entry getEntry( EntryPath path )
    {
        return entries.getEntry( path );
    }

    public Entries getEntries()
    {
        return entries;
    }

    public void setValue( final EntryPath path, final Object value )
    {
        entries.setValue( path, value );
    }

    @Override
    public String toString()
    {
        return entries.toString();
    }
}
