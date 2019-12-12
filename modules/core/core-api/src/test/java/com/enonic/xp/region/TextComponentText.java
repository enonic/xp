package com.enonic.xp.region;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
                return TextComponent.create().
                    text( "text" ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{ImageComponent.create().
                    image( ContentId.from( "image" ) ).
                    build(), TextComponent.create().
                    text( "textComponent" ).
                    build(), ImageComponent.create().
                    build(), new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return TextComponent.create().
                    text( "text" ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return TextComponent.create().
                    text( "text" ).
                    build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void copy()
    {
        final TextComponent source = TextComponent.create().text( "text" ).build();

        final TextComponent copy = source.copy();

        assertNotNull( copy );
        assertEquals( source.getType(), copy.getType() );
        assertEquals( source.getText(), copy.getText() );
        assertEquals( source.getName(), copy.getName() );
    }

}
