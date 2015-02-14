package com.enonic.xp.content.page.region;

public final class TextComponentType
    extends ComponentType
{
    public final static TextComponentType INSTANCE = new TextComponentType();

    private TextComponentType()
    {
        super( "text", TextComponent.class );
    }

}
