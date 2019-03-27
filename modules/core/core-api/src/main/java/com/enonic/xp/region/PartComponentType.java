package com.enonic.xp.region;

import com.google.common.annotations.Beta;

@Beta
public final class PartComponentType
    extends ComponentType
{
    public final static PartComponentType INSTANCE = new PartComponentType();

    private PartComponentType()
    {
        super( "part" );
    }

}
