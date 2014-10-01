package com.enonic.wem.api.content.page.text;

import com.enonic.wem.api.content.page.PageComponentType;

public final class TextComponentType
    extends PageComponentType
{
    public final static TextComponentType INSTANCE = new TextComponentType();

    private static final TextComponentDataSerializer dataSerializer = new TextComponentDataSerializer();

    private TextComponentType()
    {
        super( "text", TextComponent.class );
    }

    @Override
    public TextComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
