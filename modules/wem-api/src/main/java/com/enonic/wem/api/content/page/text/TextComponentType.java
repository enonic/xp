package com.enonic.wem.api.content.page.text;


import com.enonic.wem.api.content.page.PageComponentType;

public final class TextComponentType
    extends PageComponentType<TextComponent>
{
    private static final TextComponentDataSerializer dataSerializer = new TextComponentDataSerializer();

    public TextComponentType()
    {
        super( "text", TextComponent.class );
    }

    @Override
    public TextComponentXml toXml( final TextComponent component )
    {
        final TextComponentXml componentXml = new TextComponentXml();
        componentXml.from( component );
        return componentXml;
    }

    @Override
    public TextComponentJson toJson( final TextComponent component )
    {
        return new TextComponentJson( component );
    }

    @Override
    public TextComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
