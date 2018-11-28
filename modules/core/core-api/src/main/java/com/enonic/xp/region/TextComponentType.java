package com.enonic.xp.region;

import com.google.common.annotations.Beta;

@Beta
public final class TextComponentType
    extends ComponentType
{
    public final static TextComponentType INSTANCE = new TextComponentType();

    private TextComponentType()
    {
        super( "text" );
    }

}
