package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PartComponentType
    extends ComponentType
{
    public static final PartComponentType INSTANCE = new PartComponentType();

    private PartComponentType()
    {
        super( "part" );
    }

}
