package com.enonic.wem.api.content.page.text;

import com.enonic.wem.api.content.page.PageComponentType;

public final class TextComponentType
    extends PageComponentType
{
    private static final TextComponentDataSerializer dataSerializer = new TextComponentDataSerializer();

    public TextComponentType()
    {
        super( "text", TextComponent.class );
    }

    @Override
    public TextComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
