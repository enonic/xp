package com.enonic.xp.region;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LayoutComponentTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return createLayouComponent();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {

                return new Object[]{ImageComponent.create().
                    image( ContentId.from( "image" ) ).
                    build(), TextComponent.create().
                    text( "image" ).
                    build(), LayoutComponent.create().
                    descriptor( DescriptorKey.from( "descriptor" ) ).
                    regions( null ).
                    build(), new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return createLayouComponent();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return createLayouComponent();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void copy()
    {
        final LayoutComponent source = createLayouComponent();

        final LayoutComponent copy = source.copy();

        assertNotNull( copy );
        assertEquals( source.getType(), copy.getType() );
        assertEquals( source.getRegions(), copy.getRegions() );
        assertEquals( source.getName(), copy.getName() );
        assertEquals( source.getConfig(), copy.getConfig() );
    }

    private LayoutComponent createLayouComponent()
    {

        final PropertyTree config1 = new PropertyTree();
        config1.addString( "some", "config" );

        return LayoutComponent.create().
            config( config1 ).
            descriptor( DescriptorKey.from( "descriptor" ) ).
            regions( LayoutRegions.create().add( Region.create().
                name( "region" ).
                add( TextComponent.create().
                    text( "text" ).
                    build() ).
                build() ).
                build() ).
            build();
    }
}
