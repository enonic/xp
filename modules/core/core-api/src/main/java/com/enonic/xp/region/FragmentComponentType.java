package com.enonic.xp.region;

public final class FragmentComponentType
    extends ComponentType
{
    public static final FragmentComponentType INSTANCE = new FragmentComponentType();

    private FragmentComponentType()
    {
        super( "fragment" );
    }
}
