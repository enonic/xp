package com.enonic.xp.region;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImageComponentTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                final PropertyTree config1 = new PropertyTree();
                config1.addString( "some", "config" );

                return ImageComponent.create().
                    image( ContentId.from( "image" ) ).
                    config( config1 ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {

                return new Object[]{ImageComponent.create().
                    image( ContentId.from( "image" ) ).
                    build(), TextComponent.create().
                    text( "image" ).
                    build(), ImageComponent.create().
                    image( ContentId.from( "image" ) ).
                    build(), new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                final PropertyTree config1 = new PropertyTree();
                config1.addString( "some", "config" );

                return ImageComponent.create().
                    image( ContentId.from( "image" ) ).
                    config( config1 ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                final PropertyTree config1 = new PropertyTree();
                config1.addString( "some", "config" );

                return ImageComponent.create().
                    image( ContentId.from( "image" ) ).
                    config( config1 ).
                    build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void copy()
    {
        final PropertyTree config1 = new PropertyTree();
        config1.addString( "some", "config" );

        final ImageComponent source = ImageComponent.create().
            image( ContentId.from( "image" ) ).
            config( config1 ).
            build();

        final ImageComponent copy = source.copy();

        assertNotNull( copy );
        assertEquals( source.getType(), copy.getType() );
        assertEquals( source.getConfig(), copy.getConfig() );
        assertEquals( source.getImage(), copy.getImage() );
    }
}
