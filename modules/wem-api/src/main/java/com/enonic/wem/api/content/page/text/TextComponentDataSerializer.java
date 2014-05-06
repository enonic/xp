package com.enonic.wem.api.content.page.text;


import com.enonic.wem.api.content.page.AbstractPageComponentDataSerializer;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;

public class TextComponentDataSerializer
    extends AbstractPageComponentDataSerializer<TextComponent, TextComponent>
{

    public DataSet toData( final TextComponent component )
    {
        final DataSet asData = new DataSet( ImageComponent.class.getSimpleName() );
        applyPageComponentToData( component, asData );
        if ( component.getText() != null )
        {
            asData.addProperty( "text", Value.newContentId( component.getText() ) );
        }
        return asData;
    }

    public TextComponent fromData( final DataSet asData )
    {
        TextComponent.Builder component = TextComponent.newTextComponent();
        applyPageComponentFromData( component, asData );
        if ( asData.hasData( "text" ) )
        {
            component.text( asData.getProperty( "text" ).getString() );
        }
        return component.build();
    }
}
