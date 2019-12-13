package com.enonic.xp.page;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PageTest
{
    @Test
    public void equals()
    {
        final PropertyTree config1 = new PropertyTree();
        config1.addString( "some", "config" );

        final PropertyTree config2 = new PropertyTree();
        config2.addString( "other", "config" );

        final Region region1 = Region.create().
            name( "main" ).
            add( PartComponent.create().
                descriptor( "descriptor-x" ).
                config( new PropertyTree() ).
                build() ).
            add( PartComponent.create().
                descriptor( "descriptor-y" ).
                config( new PropertyTree() ).
                build() ).
            build();

        final Region region2 = Region.create().
            name( "apart" ).
            add( PartComponent.create().
                descriptor( "descriptor-x" ).
                config( new PropertyTree() ).
                build() ).
            add( PartComponent.create().
                descriptor( "descriptor-y" ).
                config( new PropertyTree() ).
                build() ).
            build();

        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return Page.create().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.create().add( region1 ).build() ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                Page notX1 = Page.create().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.create().add( region2 ).build() ).
                    build();

                Page notX2 = Page.create().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.create().add( region1 ).add( region2 ).build() ).
                    build();

                Page notX3 = Page.create().
                    config( config2 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.create().add( region1 ).build() ).
                    build();

                Page notX4 = Page.create().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-y" ) ).
                    regions( PageRegions.create().add( region1 ).build() ).
                    build();

                return new Object[]{notX1, notX2, notX3, notX4, new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return Page.create().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.create().add( region1 ).build() ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return Page.create().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.create().add( region1 ).build() ).
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

        final Region region1 = Region.create().
            name( "main" ).
            add( PartComponent.create().
                descriptor( "descriptor-x" ).
                config( new PropertyTree() ).
                build() ).
            add( PartComponent.create().
                descriptor( "descriptor-y" ).
                config( new PropertyTree() ).
                build() ).
            build();

        final Page sourcePage = Page.create().
            config( config1 ).
            template( PageTemplateKey.from( "template-x" ) ).
            regions( PageRegions.create().add( region1 ).build() ).
            build();

        final Page copiedPage = sourcePage.copy();

        assertEquals( sourcePage.getConfig(), copiedPage.getConfig() );
        assertEquals( sourcePage.getTemplate(), copiedPage.getTemplate() );
        assertEquals( sourcePage.getRegions(), sourcePage.getRegions() );
    }
}
