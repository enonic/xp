package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class LayoutComponentType
    extends ComponentType
{
    public static final LayoutComponentType INSTANCE = new LayoutComponentType();

    private LayoutComponentType()
    {
        super( "layout" );
    }
}
