package com.enonic.xp.page;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;

import static org.junit.Assert.*;

public class EditablePageTest
{
    @Test
    public void create_from_source()
    {
        final Page page = createPage();

        final EditablePage editablePage = new EditablePage( page );

        assertEquals( page.getConfig(), editablePage.config );
        assertEquals( page.getController(), editablePage.controller );
        assertEquals( page.getRegions(), editablePage.regions );
        assertEquals( page.getTemplate(), editablePage.template );
    }

    @Test
    public void build_page()
    {
        final EditablePage editablePage = new EditablePage( createPage() );

        final Page page = editablePage.build();

        assertEquals( editablePage.config, page.getConfig() );
        assertEquals( editablePage.controller, page.getController() );
        assertEquals( editablePage.regions, page.getRegions() );
        assertEquals( editablePage.template, page.getTemplate() );
    }

    private Page createPage()
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

        return Page.newPage().
            config( config1 ).
            template( PageTemplateKey.from( "template-x" ) ).
            regions( PageRegions.newPageRegions().add( region1 ).build() ).
            build();
    }
}
