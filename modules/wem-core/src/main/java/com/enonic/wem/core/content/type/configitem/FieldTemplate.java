package com.enonic.wem.core.content.type.configitem;

public class FieldTemplate
    extends Template
{
    private Field field;

    FieldTemplate()
    {
    }

    public String getName()
    {
        return field.getName();
    }

    @Override
    public TemplateType getType()
    {
        return TemplateType.FIELD;
    }

    public Field getField()
    {
        return field;
    }

    void setField( final Field value )
    {
        this.field = value;
    }

    public DirectAccessibleConfigItem create( final TemplateReference templateReference )
    {
        Field field = this.field.copy();
        field.setName( templateReference.getName() );
        field.setPath( templateReference.getPath() );
        return field;
    }
}
