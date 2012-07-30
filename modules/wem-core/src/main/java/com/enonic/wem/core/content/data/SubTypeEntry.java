package com.enonic.wem.core.content.data;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.FieldSet;


public class SubTypeEntry
    extends Entry
{
    private EntryPath path;

    private Entries entries;

    private FieldSet fieldSet;

    public SubTypeEntry( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.path = path;
        this.entries = new Entries( path );
    }

    public SubTypeEntry( final FieldSet fieldSet, final EntryPath path )
    {
        Preconditions.checkNotNull( fieldSet, "subType cannot be null" );
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.fieldSet = fieldSet;
        this.path = path;
        this.entries = new Entries( path, fieldSet.getConfigItems() );
    }

    public SubTypeEntry( final FieldSet fieldSet, final EntryPath path, final Entries entries )
    {
        Preconditions.checkNotNull( fieldSet, "subType cannot be null" );
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( entries, "entries cannot be null" );

        this.fieldSet = fieldSet;
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

    public Entry getEntry( final String path )
    {
        return getEntry( new EntryPath( path ) );
    }

    public Value getValue( final String path )
    {
        return entries.getValue( new EntryPath( path ) );
    }

    public Value getValue( final EntryPath path )
    {
        return entries.getValue( path );
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
