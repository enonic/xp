package com.enonic.wem.core.content;


import com.enonic.wem.core.content.config.field.FieldSet;

public class FieldSetEntries
    extends FieldEntry
{
    private FieldSet fieldSet;

    public FieldSetEntries( final FieldSet fieldSet )
    {
        this.fieldSet = fieldSet;
    }

    @Override
    public FieldEntryPath getPath()
    {
        return null;
    }

    @Override
    public FieldEntryJsonGenerator getJsonGenerator()
    {
        return null;
    }
}
