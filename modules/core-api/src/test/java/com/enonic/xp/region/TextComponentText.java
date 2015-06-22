package com.enonic.xp.region;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.Assert.*;

public class TextComponentText
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return TextComponent.newTextComponent().
                    text( "text" ).
                    name( "textComponent" ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{ImageComponent.newImageComponent().
                    image( ContentId.from( "image" ) ).
                    name( "imageComponent" ).
                    build(), TextComponent.newTextComponent().text( "textComponent" ).name( "text" ).build(),
                    ImageComponent.newImageComponent().
                        name( "text" ).
                        build(), new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return TextComponent.newTextComponent().
                    text( "text" ).
                    name( "textComponent" ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return TextComponent.newTextComponent().
                    text( "text" ).
                    name( "textComponent" ).
                    build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void copy()
    {
        final TextComponent source = TextComponent.newTextComponent().text( "text" ).name( "name" ).build();

        final TextComponent copy = source.copy();

        assertNotNull( copy );
        assertEquals( source.getType(), copy.getType() );
        assertEquals( source.getText(), copy.getText() );
        assertEquals( source.getName(), copy.getName() );
    }

}
