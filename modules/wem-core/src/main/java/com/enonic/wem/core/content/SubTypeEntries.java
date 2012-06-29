package com.enonic.wem.core.content;


import com.enonic.wem.core.content.config.field.SubType;

public class SubTypeEntries
    extends FieldEntry
{
    private SubType subType;

    private FieldEntryPath fieldEntryPath;

    private FieldEntries fieldEntries;

    public SubTypeEntries( final SubType subType, final FieldEntryPath fieldEntryPath )
    {
        this.subType = subType;
        this.fieldEntryPath = fieldEntryPath;
        this.fieldEntries = new FieldEntries( subType.getConfigItems() );
    }

    @Override
    public FieldEntryPath getPath()
    {
        return fieldEntryPath;
    }

    public FieldEntries getFieldEntries()
    {
        return fieldEntries;
    }

    public void setFieldValue( final FieldEntryPath fieldEntryPath, final Object value )
    {
        fieldEntries.setFieldValue( fieldEntryPath, value );
    }

    @Override
    public FieldEntryJsonGenerator getJsonGenerator()
    {
        return SubTypeEntriesJsonGenerator.DEFAULT;
    }
}
