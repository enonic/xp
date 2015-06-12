package com.enonic.xp.region;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.Assert.*;

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

                return new Object[]{ImageComponent.newImageComponent().
                    image( ContentId.from( "image" ) ).
                    name( "imageComponent" ).
                    build(), TextComponent.newTextComponent().text( "image" ).name( "imageComponent" ).build(),
                    LayoutComponent.newLayoutComponent().
                        name( "name" ).
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

        final PropertyTree config1 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        config1.addString( "some", "config" );

        return LayoutComponent.newLayoutComponent().
            name( "name" ).
            config( config1 ).
            descriptor( DescriptorKey.from( "descriptor" ) ).
            regions( LayoutRegions.newLayoutRegions().add( Region.newRegion().
                name( "region" ).
                add( TextComponent.newTextComponent().
                    text( "text" ).
                    name( "textComponent" ).
                    build() ).
                build() ).
                build() ).
            build();
    }
}
