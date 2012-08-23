package com.enonic.wem.core.content.type.configitem;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.module.Module;

public class FieldTemplateBuilder
{
    private Field field;

    private Module module;

    public FieldTemplateBuilder field( Field value )
    {
        this.field = value;
        return this;
    }

    public FieldTemplateBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public FieldTemplate build()
    {
        Preconditions.checkNotNull( field, "field is required" );

        FieldTemplate fieldTemplate = new FieldTemplate();
        fieldTemplate.setField( field );
        fieldTemplate.setModule( module );
        return fieldTemplate;
    }

    public static FieldTemplateBuilder newFieldTemplate()
    {
        return new FieldTemplateBuilder();
    }
}
