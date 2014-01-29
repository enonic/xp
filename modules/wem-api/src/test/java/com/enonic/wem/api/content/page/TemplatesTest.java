package com.enonic.wem.api.content.page;

import org.junit.Test;

import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.enonic.wem.api.content.page.layout.LayoutRegions.newLayoutRegions;
import static org.junit.Assert.*;

public class TemplatesTest
{

    @Test
    public void pageTemplate()
    {
        RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", new Value.Long( 10000 ) );

        PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "mainmodule|main-page" ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( PageDescriptorKey.from( ModuleKey.from( "mainmodule-1.0.0" ), new ComponentDescriptorName( "landing-page" ) ) ).
            build();

        assertEquals( "main-page", pageTemplate.getName().toString() );
        assertTrue( pageTemplate.getCanRender().contains( ContentTypeName.from( "article" ) ) );
    }

    @Test
    public void partTemplate()
    {
        RootDataSet partTemplateConfig = new RootDataSet();
        partTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        PartTemplate partTemplate = PartTemplate.newPartTemplate().
            key( PartTemplateKey.from( "mainmodule|news-part" ) ).
            displayName( "News part template" ).
            config( partTemplateConfig ).
            descriptor( PartDescriptorKey.from( ModuleKey.from( "mainmodule-1.0.0" ), new ComponentDescriptorName( "news-part" ) ) ).
            build();

        assertEquals( "news-part", partTemplate.getName().toString() );
    }

    @Test
    public void layoutTemplate()
    {
        RootDataSet layoutTemplateConfig = new RootDataSet();
        layoutTemplateConfig.addProperty( "columns", new Value.Long( 3 ) );

        LayoutTemplate layoutTemplate = LayoutTemplate.newLayoutTemplate().
            key( LayoutTemplateKey.from( "mainmodule|my-layout" ) ).
            displayName( "Layout template" ).
            config( layoutTemplateConfig ).
            descriptor( LayoutDescriptorKey.from( ModuleKey.from( "mainmodule-1.0.0" ), new ComponentDescriptorName( "some-layout" ) ) ).
            regions( newLayoutRegions().build() ).
            build();

        assertEquals( "my-layout", layoutTemplate.getName().toString() );
    }
}
