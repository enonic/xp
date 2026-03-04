package com.enonic.xp.region;

public final class TextComponentType
    extends ComponentType
{
    public static final TextComponentType INSTANCE = new TextComponentType();

    private TextComponentType()
    {
        super( "text" );
    }

}
