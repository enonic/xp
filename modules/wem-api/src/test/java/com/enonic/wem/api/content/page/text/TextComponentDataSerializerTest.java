package com.enonic.wem.api.content.page.text;

import org.junit.Test;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;

import static org.junit.Assert.*;

public class TextComponentDataSerializerTest
{
    @Test
    public void toData()
    {
        // setup
        TextComponent textComponent = TextComponent.newTextComponent().name( "myText" ).text( "some text" ).build();

        // exercise
        DataSet data = new TextComponentDataSerializer().toData( textComponent );

        // verify
        assertEquals( "TextComponent", data.getName() );
        assertEquals( "myText", data.getProperty( "name" ).getString() );
        assertEquals( "some text", data.getProperty( "text" ).getString() );
    }

    @Test
    public void fromData()
    {
        // setup
        DataSet data = new DataSet( "TextComponent" );
        data.setProperty( "name", Value.newString( "myText" ) );
        data.setProperty( "text", Value.newString( "some text" ) );

        // exercise
        TextComponent textComponent = new TextComponentDataSerializer().fromData( data );

        // verify
        assertEquals( "myText", textComponent.getName().toString() );
        assertEquals( "some text", textComponent.getText() );
    }
}