package com.enonic.wem.api.content.type.formitem;


import com.enonic.wem.api.module.Module;

public class ComponentSetSubTypeBuilder
{
    private Module module;

    private ComponentSet componentSet;

    public ComponentSetSubTypeBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public ComponentSetSubTypeBuilder componentSet( ComponentSet value )
    {
        this.componentSet = value;
        return this;
    }

    public ComponentSetSubType build()
    {
        ComponentSetSubType subType = new ComponentSetSubType();
        subType.setModule( module );
        subType.setComponentSet( componentSet );

        return subType;
    }

    public static ComponentSetSubTypeBuilder newComponentSetSubType()
    {
        return new ComponentSetSubTypeBuilder();
    }
}
