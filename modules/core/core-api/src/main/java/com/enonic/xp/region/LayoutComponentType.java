package com.enonic.xp.region;

import com.google.common.annotations.Beta;

@Beta
public final class LayoutComponentType
    extends ComponentType
{
    public final static LayoutComponentType INSTANCE = new LayoutComponentType();

    private LayoutComponentType()
    {
        super( "layout" );
    }
}
