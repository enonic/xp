package com.enonic.wem.core.content.data;


import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.SubType;

public class ContentData
{
    private Entries entries;

    /**
     * Structured data.
     *
     * @param configItems
     */
    public ContentData( final ConfigItems configItems )
    {
        this.entries = new Entries( new EntryPath(), configItems );
    }

    /**
     * Unstructured data.
     */
    public ContentData()
    {
        this.entries = new Entries( new EntryPath() );
    }

    void setEntries( final Entries entries )
    {
        this.entries = entries;
    }

    public void setValue( final EntryPath name, final Object value )
    {
        entries.setValue( name, value );
    }

    public void setValue( final String fieldEntryPath, final Object value )
    {
        entries.setValue( new EntryPath( fieldEntryPath ), value );
    }

    public void setValue( final String fieldEntryPath, final SubType value )
    {
        entries.setValue( new EntryPath( fieldEntryPath ), value );
    }

    public Value getValue( final String path )
    {
        return entries.getValue( new EntryPath( path ) );
    }

    public Value getValue( final EntryPath path )
    {
        return entries.getValue( path );
    }

    public SubTypeEntry getSubTypeEntry( final String path )
    {
        return getSubTypeEntry( new EntryPath( path ) );
    }

    public SubTypeEntry getSubTypeEntry( final EntryPath path )
    {
        return entries.getSubTypeEntry( path );
    }

    Entries getEntries()
    {
        return entries;
    }
}
