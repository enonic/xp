package com.enonic.wem.core.content.data;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.valuetype.BasalValueType;

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

    public void setConfigItems( final ConfigItems configItems )
    {
        this.entries.setConfigItems( configItems );
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

    public String getValueAsString( final EntryPath path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        Value value = getValue( path );
        Preconditions.checkNotNull( value, "No value at path: " + path );

        Preconditions.checkArgument( value.getBasalValueType() == BasalValueType.STRING, "Value is not of type %", BasalValueType.STRING );
        return (String) value.getValue();
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

    public boolean breaksRequiredContract()
    {
        return entries.breaksRequiredContract();
    }
}
