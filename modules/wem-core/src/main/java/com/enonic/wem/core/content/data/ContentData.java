package com.enonic.wem.core.content.data;


import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.FieldSet;

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

    public void setValue( final EntryPath path, final Object value )
    {
        entries.setValue( path, value );
    }

    public void setValue( final EntryPath path, final String value )
    {
        entries.setValue( path, value );
    }

    public void setValue( final String path, final Object value )
    {
        entries.setValue( new EntryPath( path ), value );
    }

    public void setValue( final String path, final FieldSet value )
    {
        entries.setValue( new EntryPath( path ), value );
    }

    public Value getValue( final String path )
    {
        return entries.getValue( new EntryPath( path ) );
    }

    public Value getValue( final EntryPath path )
    {
        return entries.getValue( path );
    }

    public Entries getEntries( final String path )
    {
        return getEntries( new EntryPath( path ) );
    }

    public Entries getEntries( final EntryPath path )
    {
        return entries.getEntries( path );
    }

    Entries getEntries()
    {
        return entries;
    }
}
