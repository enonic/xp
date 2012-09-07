package com.enonic.wem.core.content.type.configitem;

public class FieldTemplate
    extends Template
{
    private Component component;

    FieldTemplate()
    {
    }

    public String getName()
    {
        return component.getName();
    }

    @Override
    public TemplateType getType()
    {
        return TemplateType.FIELD;
    }

    public Component getComponent()
    {
        return component;
    }

    void setComponent( final Component value )
    {
        this.component = value;
    }

    public DirectAccessibleFormItem create( final TemplateReference templateReference )
    {
        Component component = this.component.copy();
        component.setName( templateReference.getName() );
        component.setPath( templateReference.getPath() );
        return component;
    }
}
