package com.enonic.xp.core.impl.content.page.region;


import com.enonic.xp.content.page.ComponentDataSerializer;
import com.enonic.xp.content.page.region.TextComponent;
import com.enonic.xp.data.PropertySet;

public class TextComponentDataSerializer
    extends ComponentDataSerializer<TextComponent, TextComponent>
{

    public void toData( final TextComponent component, final PropertySet parent )
    {
        final PropertySet asData = parent.addSet( TextComponent.class.getSimpleName() );
        applyComponentToData( component, asData );
        if ( component.getText() != null )
        {
            asData.addString( "text", component.getText() );
        }
    }

    public TextComponent fromData( final PropertySet asData )
    {
        TextComponent.Builder component = TextComponent.newTextComponent();
        applyComponentFromData( component, asData );
        if ( asData.isNotNull( "text" ) )
        {
            component.text( asData.getString( "text" ) );
        }
        return component.build();
    }
}
