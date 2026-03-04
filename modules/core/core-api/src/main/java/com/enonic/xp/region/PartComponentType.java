package com.enonic.xp.region;

public final class PartComponentType
    extends ComponentType
{
    public static final PartComponentType INSTANCE = new PartComponentType();

    private PartComponentType()
    {
        super( "part" );
    }

}
