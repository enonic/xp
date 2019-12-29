package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class TextComponentType
    extends ComponentType
{
    public final static TextComponentType INSTANCE = new TextComponentType();

    private TextComponentType()
    {
        super( "text" );
    }

}
