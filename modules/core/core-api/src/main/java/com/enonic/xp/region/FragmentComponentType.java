package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class FragmentComponentType
    extends ComponentType
{
    public static final FragmentComponentType INSTANCE = new FragmentComponentType();

    private FragmentComponentType()
    {
        super( "fragment" );
    }
}
