package com.enonic.xp.core.impl.content.serializer;

import org.junit.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.region.TextComponentType;

import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.COMPONENTS;
import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.TYPE;
import static org.junit.Assert.*;

public class TextComponentDataSerializerTest
{
    @Test
    public void toData()
    {
        // setup
        final String text = "some text";
        final TextComponent textComponent = TextComponent.create().text( text ).build();
        final PropertyTree tree = new PropertyTree();

        // exercise
        new TextComponentDataSerializer().toData( textComponent, tree.getRoot() );

        // verify
        final PropertySet set = tree.getSet( COMPONENTS );
        assertEquals( TextComponentType.INSTANCE.toString(), set.getString( TYPE ) );
        assertEquals( text, set.getSet( TextComponentType.INSTANCE.toString() ).getString( "value" ) );
    }

    @Test
    public void fromData()
    {
        // setup
        final String text = "some text";
        final PropertySet data = new PropertyTree().addSet( COMPONENTS );
        data.setString( TYPE, TextComponentType.INSTANCE.toString() );
        final PropertySet specBlock = data.addSet( TextComponentType.INSTANCE.toString() );
        specBlock.addString( "value", text );

        // exercise
        final TextComponent textComponent = new TextComponentDataSerializer().fromData( data );

        // verify
        assertEquals( "Text", textComponent.getName().toString() );
        assertEquals( text, textComponent.getText() );
    }
}