package com.enonic.wem.api.content.page;

import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKeys;
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
            id( new PageTemplateId( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/landing-page.xml" ) ).
            build();

        assertEquals( "1fad493a-6a72-41a3-bac4-88aba3d83bcc", pageTemplate.getId().toString() );
        assertTrue( pageTemplate.getCanRender().contains( ContentTypeName.from( "article" ) ) );
    }

    @Test
    public void partTemplate()
    {
        RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        PartTemplate partTemplate = PartTemplate.newPartTemplate().
            id( new PartTemplateId( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            displayName( "News part template" ).
            config( pageTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/news-part.xml" ) ).
            build();

        assertEquals( "1fad493a-6a72-41a3-bac4-88aba3d83bcc", partTemplate.getId().toString() );
    }

    @Test
    public void layoutTemplate()
    {
        RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "columns", new Value.Long( 3 ) );

        LayoutTemplate partTemplate = LayoutTemplate.newLayoutTemplate().
            id( new LayoutTemplateId( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            displayName( "Layout template" ).
            config( pageTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/some-layout.xml" ) ).
            build();

        assertEquals( "1fad493a-6a72-41a3-bac4-88aba3d83bcc", partTemplate.getId().toString() );
    }

    @Test
    public void siteTemplate()
    {
        SiteTemplate partTemplate = SiteTemplate.newSiteTemplate().
            id( new SiteTemplateId( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            displayName( "Enonic Intranet" ).
            description( "A social intranet for the Enterprise" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            modules( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                      "com.enonic.resolvers-1.0.0" ) ).
            supportedContentTypes( ContentTypeNames.from( "com.enonic.intranet", "system.folder" ) ).
            rootContentType( ContentTypeName.from( "com.enonic.intranet" ) ).
            build();

        assertEquals( "1fad493a-6a72-41a3-bac4-88aba3d83bcc", partTemplate.getId().toString() );
    }

}
