package com.enonic.wem.api.content.type.formitem;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.Module;

public class ComponentSubTypeBuilder
{
    private Component component;

    private Module module;

    public ComponentSubTypeBuilder component( Component value )
    {
        this.component = value;
        return this;
    }

    public ComponentSubTypeBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public ComponentSubType build()
    {
        Preconditions.checkNotNull( component, "component is required" );

        ComponentSubType subType = new ComponentSubType();
        subType.setComponent( component );
        subType.setModule( module );
        return subType;
    }

    public static ComponentSubTypeBuilder newComponentSubType()
    {
        return new ComponentSubTypeBuilder();
    }
}
