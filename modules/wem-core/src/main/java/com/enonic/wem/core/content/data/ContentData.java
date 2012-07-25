package com.enonic.wem.core.content.data;


import com.enonic.wem.core.content.type.item.ConfigItems;
import com.enonic.wem.core.content.type.item.SubType;

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
        this.entries = new Entries( new ValuePath(), configItems );
    }

    /**
     * Unstructured data.
     */
    public ContentData()
    {
        this.entries = new Entries( new ValuePath() );
    }

    void setEntries( final Entries entries )
    {
        this.entries = entries;
    }

    public void setValue( final ValuePath name, final Object value )
    {
        entries.setValue( name, value );
    }

    public void setValue( final String fieldEntryPath, final Object value )
    {
        entries.setValue( new ValuePath( fieldEntryPath ), value );
    }

    public void setValue( final String fieldEntryPath, final SubType value )
    {
        entries.setValue( new ValuePath( fieldEntryPath ), value );
    }

    public Entries getEntries()
    {
        return entries;
    }
}
