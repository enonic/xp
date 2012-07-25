package com.enonic.wem.core.content.data;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.type.item.SubType;

public class SubTypeEntry
    extends Entry
{
    private ValuePath path;

    private Entries entries;

    private SubType subType;

    public SubTypeEntry( final ValuePath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.path = path;
        this.entries = new Entries( path );
    }

    public SubTypeEntry( final SubType subType, final ValuePath path )
    {
        Preconditions.checkNotNull( subType, "subType cannot be null" );
        Preconditions.checkNotNull( path, "path cannot be null" );

        this.subType = subType;
        this.path = path;
        this.entries = new Entries( path, subType.getConfigItems() );
    }

    public SubTypeEntry( final SubType subType, final ValuePath path, final Entries entries )
    {
        Preconditions.checkNotNull( subType, "subType cannot be null" );
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( entries, "entries cannot be null" );

        this.subType = subType;
        this.path = path;
        this.entries = entries;
    }

    public SubTypeEntry( final ValuePath path, final Entries entries )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        Preconditions.checkNotNull( entries, "entries cannot be null" );

        this.path = path;
        this.entries = entries;
    }

    @Override
    public ValuePath getPath()
    {
        return path;
    }

    public Entries getEntries()
    {
        return entries;
    }

    public void setValue( final ValuePath path, final Object value )
    {
        entries.setValue( path, value );
    }

    @Override
    public String toString()
    {
        return entries.toString();
    }
}
