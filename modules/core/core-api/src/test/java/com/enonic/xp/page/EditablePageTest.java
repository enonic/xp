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
        assertEquals( page.getDescriptor(), editablePage.controller );
        assertEquals( page.getRegions(), editablePage.regions );
        assertEquals( page.getTemplate(), editablePage.template );
    }

    @Test
    public void build_page()
    {
        final EditablePage editablePage = new EditablePage( createPage() );

        final Page page = editablePage.build();

        assertEquals( editablePage.config, page.getConfig() );
        assertEquals( editablePage.controller, page.getDescriptor() );
        assertEquals( editablePage.regions, page.getRegions() );
        assertEquals( editablePage.template, page.getTemplate() );
    }

    private Page createPage()
    {
        final PropertyTree config1 = new PropertyTree();
        config1.addString( "some", "config" );

        final Region region1 = Region.create().
            name( "main" ).
            add( PartComponent.create().
                name( "MyPart" ).
                descriptor( "descriptor-x" ).
                config( new PropertyTree() ).
                build() ).
            add( PartComponent.create().
                name( "MyOtherPart" ).
                descriptor( "descriptor-y" ).
                config( new PropertyTree() ).
                build() ).
            build();

        return Page.create().
            config( config1 ).
            template( PageTemplateKey.from( "template-x" ) ).
            regions( PageRegions.create().add( region1 ).build() ).
            build();
    }
}
