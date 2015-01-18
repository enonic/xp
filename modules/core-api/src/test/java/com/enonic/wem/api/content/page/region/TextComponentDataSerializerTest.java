package com.enonic.wem.api.content.page.region;

import org.junit.Test;

import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;

import static org.junit.Assert.*;

public class TextComponentDataSerializerTest
{
    @Test
    public void toData()
    {
        // setup
        TextComponent textComponent = TextComponent.newTextComponent().name( "myText" ).text( "some text" ).build();
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

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
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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