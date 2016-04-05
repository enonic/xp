package com.enonic.xp.region;

import com.google.common.annotations.Beta;

@Beta
public final class FragmentComponentType
    extends ComponentType
{
    public final static FragmentComponentType INSTANCE = new FragmentComponentType();

    private FragmentComponentType()
    {
        super( "fragment", FragmentComponent.class );
    }
}
