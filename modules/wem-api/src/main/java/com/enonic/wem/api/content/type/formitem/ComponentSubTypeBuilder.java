package com.enonic.wem.api.content.type.formitem;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.Module;

public class ComponentSubTypeBuilder
{
    private Input input;

    private Module module;

    public ComponentSubTypeBuilder input( Input value )
    {
        this.input = value;
        return this;
    }

    public ComponentSubTypeBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public ComponentSubType build()
    {
        Preconditions.checkNotNull( input, "input is required" );

        ComponentSubType subType = new ComponentSubType();
        subType.setInput( input );
        subType.setModule( module );
        return subType;
    }

    public static ComponentSubTypeBuilder newComponentSubType()
    {
        return new ComponentSubTypeBuilder();
    }
}
