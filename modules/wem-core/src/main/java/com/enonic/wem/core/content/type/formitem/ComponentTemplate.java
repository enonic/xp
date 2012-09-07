package com.enonic.wem.core.content.type.formitem;

public class ComponentTemplate
    extends Template
{
    private Component component;

    ComponentTemplate()
    {
    }

    public String getName()
    {
        return component.getName();
    }

    @Override
    public TemplateType getType()
    {
        return TemplateType.COMPONENT;
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
