package com.enonic.wem.api.content.type.formitem;

public class ComponentSubType
    extends SubType
{
    private Input input;

    ComponentSubType()
    {
    }

    public String getName()
    {
        return input.getName();
    }

    @Override
    public Class getType()
    {
        return this.getClass();
    }

    public Input getInput()
    {
        return input;
    }

    void setInput( final Input value )
    {
        this.input = value;
    }

    public HierarchicalFormItem create( final SubTypeReference subTypeReference )
    {
        Input input = this.input.copy();
        input.setName( subTypeReference.getName() );
        input.setPath( subTypeReference.getPath() );
        return input;
    }
}
