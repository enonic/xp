package com.enonic.xp.core.impl.content.serializer;


import com.enonic.xp.data.PropertySet;
import com.enonic.xp.region.TextComponent;

public class TextComponentDataSerializer
    extends ComponentDataSerializer<TextComponent, TextComponent>
{

    @Override
    public void toData( final TextComponent component, final PropertySet parent )
    {
        final PropertySet asData = parent.addSet( TextComponent.class.getSimpleName() );
        applyComponentToData( component, asData );
        if ( component.getText() != null )
        {
            asData.addString( "text", component.getText() );
        }
    }

    @Override
    public TextComponent fromData( final PropertySet asData )
    {
        TextComponent.Builder component = TextComponent.create();
        applyComponentFromData( component, asData );
        if ( asData.isNotNull( "text" ) )
        {
            component.text( asData.getString( "text" ) );
        }
        return component.build();
    }
}
