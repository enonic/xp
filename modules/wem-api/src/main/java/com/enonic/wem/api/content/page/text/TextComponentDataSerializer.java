package com.enonic.wem.api.content.page.text;


import com.enonic.wem.api.content.page.AbstractPageComponentDataSerializer;
import com.enonic.wem.api.data.PropertySet;

public class TextComponentDataSerializer
    extends AbstractPageComponentDataSerializer<TextComponent, TextComponent>
{

    public void toData( final TextComponent component, final PropertySet parent )
    {
        final PropertySet asData = parent.addSet( TextComponent.class.getSimpleName() );
        applyPageComponentToData( component, asData );
        if ( component.getText() != null )
        {
            asData.addString( "text", component.getText() );
        }
    }

    public TextComponent fromData( final PropertySet asData )
    {
        TextComponent.Builder component = TextComponent.newTextComponent();
        applyPageComponentFromData( component, asData );
        if ( asData.isNotNull( "text" ) )
        {
            component.text( asData.getString( "text" ) );
        }
        return component.build();
    }
}
