package com.enonic.wem.api.content.page;

import org.junit.Test;

import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateName;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateName;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static org.junit.Assert.*;

public class TemplatesTest
{

    @Test
    public void pageTemplate()
    {
        RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", new Value.Long( 10000 ) );

        PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            name( new PageTemplateName( "main-page" ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/landing-page.xml" ) ).
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
            name( new PartTemplateName( "news-part" ) ).
            displayName( "News part template" ).
            config( partTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/news-part.xml" ) ).
            build();

        assertEquals( "news-part", partTemplate.getName().toString() );
    }

    @Test
    public void layoutTemplate()
    {
        RootDataSet layoutTemplateConfig = new RootDataSet();
        layoutTemplateConfig.addProperty( "columns", new Value.Long( 3 ) );

        LayoutTemplate layoutTemplate = LayoutTemplate.newLayoutTemplate().
            name( new LayoutTemplateName( "my-layout" ) ).
            displayName( "Layout template" ).
            config( layoutTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/some-layout.xml" ) ).
            build();

        assertEquals( "my-layout", layoutTemplate.getName().toString() );
    }
}
