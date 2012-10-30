package com.enonic.wem.api.content.type.formitem;

public class ComponentSubType
    extends SubType
{
    private Component component;

    ComponentSubType()
    {
    }

    public String getName()
    {
        return component.getName();
    }

    @Override
    public Class getType()
    {
        return this.getClass();
    }

    public Component getComponent()
    {
        return component;
    }

    void setComponent( final Component value )
    {
        this.component = value;
    }

    public HierarchicalFormItem create( final SubTypeReference subTypeReference )
    {
        Component component = this.component.copy();
        component.setName( subTypeReference.getName() );
        component.setPath( subTypeReference.getPath() );
        return component;
    }
}
