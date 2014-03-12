package com.enonic.wem.core.content.page.text;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.text.TextComponent;
import com.enonic.wem.api.content.page.text.TextDescriptorKey;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.core.content.page.PageComponentDataSerializer;

public class TextComponentDataSerializer
    extends PageComponentDataSerializer<TextComponent, TextComponent>
{

    public DataSet toData( final TextComponent component )
    {
        final DataSet asData = new DataSet( TextComponent.class.getSimpleName() );
        applyPageComponentToData( component, asData );
        return asData;
    }

    public TextComponent fromData( final DataSet asData )
    {
        TextComponent.Builder component = TextComponent.newTextComponent();
        applyPageComponentFromData( component, asData );
        return component.build();
    }

    @Override
    protected DescriptorKey toDescriptorkey( final String s )
    {
        return TextDescriptorKey.from( s );
    }

}
