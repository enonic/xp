package com.enonic.xp.content.page;

import org.junit.Test;

import com.enonic.xp.content.page.region.PartComponent;
import com.enonic.xp.content.page.region.Region;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.support.AbstractEqualsTest;

public class PageTest
{
    @Test
    public void equals()
    {
        PropertyTree config1 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        config1.addString( "some", "config" );

        PropertyTree config2 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        config2.addString( "other", "config" );

        Region region1 = Region.newRegion().
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

        Region region2 = Region.newRegion().
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

                return new Object[]{notX1, notX2, notX3, notX4};
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
}