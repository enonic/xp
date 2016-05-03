package com.enonic.xp.core.impl.content.page.region;

import org.junit.Test;

import com.enonic.xp.core.impl.content.serializer.TextComponentDataSerializer;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.region.TextComponent;

import static org.junit.Assert.*;

public class TextComponentDataSerializerTest
{
    @Test
    public void toData()
    {
        // setup
        TextComponent textComponent = TextComponent.create().name( "myText" ).text( "some text" ).build();
        PropertyTree tree = new PropertyTree();

        // exercise
        new TextComponentDataSerializer().toData( textComponent, tree.getRoot() );

        // verify
        PropertySet set = tree.getSet( TextComponent.class.getSimpleName() );
        assertEquals( "myText", set.getString( "name" ) );
        assertEquals( "some text", set.getString( "text" ) );
    }

    @Test
    public void fromData()
    {
        // setup
        PropertyTree tree = new PropertyTree();
        PropertySet data = tree.addSet( TextComponent.class.getSimpleName() );
        data.setString( "name", "myText" );
        data.setString( "text", "some text" );

        // exercise
        TextComponent textComponent = new TextComponentDataSerializer().fromData( data );

        // verify
        assertEquals( "myText", textComponent.getName().toString() );
        assertEquals( "some text", textComponent.getText() );
    }
}