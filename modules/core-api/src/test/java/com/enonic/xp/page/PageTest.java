package com.enonic.xp.page;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.region.PartComponent;
import com.enonic.xp.page.region.Region;
import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.Assert.*;

public class PageTest
{
    @Test
    public void equals()
    {
        final PropertyTree config1 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        config1.addString( "some", "config" );

        final PropertyTree config2 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        config2.addString( "other", "config" );

        final Region region1 = Region.newRegion().
            name( "main" ).
            add( PartComponent.newPartComponent().
                name( "MyPart" ).
                descriptor( "descriptor-x" ).
                config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
                build() ).
            add( PartComponent.newPartComponent().
                name( "MyOtherPart" ).
                descriptor( "descriptor-y" ).
                config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
                build() ).
            build();

        final Region region2 = Region.newRegion().
            name( "apart" ).
            add( PartComponent.newPartComponent().
                name( "MyPart" ).
                descriptor( "descriptor-x" ).
                config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
                build() ).
            add( PartComponent.newPartComponent().
                name( "MyOtherPart" ).
                descriptor( "descriptor-y" ).
                config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
                build() ).
            build();

        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return Page.newPage().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.newPageRegions().add( region1 ).build() ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                Page notX1 = Page.newPage().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.newPageRegions().add( region2 ).build() ).
                    build();

                Page notX2 = Page.newPage().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.newPageRegions().add( region1 ).add( region2 ).build() ).
                    build();

                Page notX3 = Page.newPage().
                    config( config2 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.newPageRegions().add( region1 ).build() ).
                    build();

                Page notX4 = Page.newPage().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-y" ) ).
                    regions( PageRegions.newPageRegions().add( region1 ).build() ).
                    build();

                return new Object[]{notX1, notX2, notX3, notX4, new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return Page.newPage().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.newPageRegions().add( region1 ).build() ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return Page.newPage().
                    config( config1 ).
                    template( PageTemplateKey.from( "template-x" ) ).
                    regions( PageRegions.newPageRegions().add( region1 ).build() ).
                    build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void copy()
    {
        final PropertyTree config1 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        config1.addString( "some", "config" );

        final Region region1 = Region.newRegion().
            name( "main" ).
            add( PartComponent.newPartComponent().
                name( "MyPart" ).
                descriptor( "descriptor-x" ).
                config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
                build() ).
            add( PartComponent.newPartComponent().
                name( "MyOtherPart" ).
                descriptor( "descriptor-y" ).
                config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
                build() ).
            build();

        final Page sourcePage = Page.newPage().
            config( config1 ).
            template( PageTemplateKey.from( "template-x" ) ).
            regions( PageRegions.newPageRegions().add( region1 ).build() ).
            build();

        final Page copiedPage = sourcePage.copy();

        assertEquals( sourcePage.getConfig(), copiedPage.getConfig() );
        assertEquals( sourcePage.getTemplate(), copiedPage.getTemplate() );
        assertEquals( sourcePage.getRegions(), sourcePage.getRegions() );
    }
}