package com.enonic.wem.core.content;


import com.enonic.wem.core.content.config.field.ConfigItems;

public class ContentData
{

    private FieldEntries fieldEntries;

    public ContentData( final ConfigItems configItems )
    {
        this.fieldEntries = new FieldEntries( configItems );
    }

    void setFieldEntries( final FieldEntries fieldEntries )
    {
        this.fieldEntries = fieldEntries;
    }

    public void setFieldValue( final FieldEntryPath name, final Object value )
    {
        fieldEntries.setFieldValue( name, value );
    }

    public void setFieldValue( final String fieldEntryPath, final Object value )
    {
        fieldEntries.setFieldValue( new FieldEntryPath( fieldEntryPath ), value );
    }

    public FieldEntries getFieldEntries()
    {
        return fieldEntries;
    }
}
