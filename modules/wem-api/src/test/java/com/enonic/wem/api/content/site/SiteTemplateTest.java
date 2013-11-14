package com.enonic.wem.api.content.site;

import org.junit.Test;

import com.google.common.collect.Iterators;

import com.enonic.wem.api.content.page.LayoutTemplate;
import com.enonic.wem.api.content.page.LayoutTemplateName;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.page.PartTemplateName;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.enonic.wem.api.content.site.ContentTypeFilter.newContentFilter;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static org.junit.Assert.*;

public class SiteTemplateTest
{

    @Test
    public void siteTemplate()
    {
        final ContentTypeFilter contentTypeFilter =
            newContentFilter().defaultDeny().allowContentTypes( ContentTypeNames.from( "com.enonic.intranet", "system.folder" ) ).build();
        SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "Intranet-1.0.0" ) ).
            displayName( "Enonic Intranet" ).
            info( "A social intranet for the Enterprise" ).
            vendor( newVendor().name( "Enonic" ).url( "https://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                      "com.enonic.resolvers-1.0.0" ) ).
            contentTypeFilter( contentTypeFilter ).
            rootContentType( ContentTypeName.from( "com.enonic.intranet" ) ).
            build();

        assertEquals( SiteTemplateKey.from( "Intranet-1.0.0" ), siteTemplate.getKey() );
        assertEquals( new SiteTemplateName( "Intranet" ), siteTemplate.getName() );
        assertEquals( new SiteTemplateVersion( "1.0.0" ), siteTemplate.getVersion() );
        assertEquals( "Enonic Intranet", siteTemplate.getDisplayName() );
        assertEquals( "A social intranet for the Enterprise", siteTemplate.getInfo() );
        assertEquals( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                       "com.enonic.resolvers-1.0.0" ), siteTemplate.getModules() );
        assertEquals( contentTypeFilter, siteTemplate.getContentTypeFilter() );
        assertEquals( ContentTypeName.from( "com.enonic.intranet" ), siteTemplate.getRootContentType() );
        assertEquals( "Enonic", siteTemplate.getVendor().getName() );
        assertEquals( "https://www.enonic.com", siteTemplate.getVendor().getUrl() );
    }

    @Test
    public void siteTemplateWithResources()
    {
        final RootDataSet partTemplateConfig = new RootDataSet();
        partTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        final PartTemplate partTemplate = PartTemplate.newPartTemplate().
            name( new PartTemplateName( "news-part" ) ).
            displayName( "News part template" ).
            config( partTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/news-part.xml" ) ).
            build();
        final RootDataSet layoutTemplateConfig = new RootDataSet();
        layoutTemplateConfig.addProperty( "columns", new Value.Long( 3 ) );

        final LayoutTemplate layoutTemplate = LayoutTemplate.newLayoutTemplate().
            name( new LayoutTemplateName( "my-layout" ) ).
            displayName( "Layout template" ).
            config( layoutTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/some-layout.xml" ) ).
            build();

        final RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", new Value.Long( 10000 ) );

        final PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            name( new PageTemplateName( "main-page" ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/landing-page.xml" ) ).
            build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "Intranet-1.0.0" ) ).
            displayName( "Enonic Intranet" ).
            info( "A social intranet for the Enterprise" ).
            addTemplate( ResourcePath.from( "components/pages/" ), pageTemplate ).
            addTemplate( partTemplate ).
            addTemplate( layoutTemplate ).
            build();

        assertEquals( SiteTemplateKey.from( "Intranet-1.0.0" ), siteTemplate.getKey() );
        assertEquals( new SiteTemplateName( "Intranet" ), siteTemplate.getName() );
        assertEquals( new SiteTemplateVersion( "1.0.0" ), siteTemplate.getVersion() );
        assertEquals( "Enonic Intranet", siteTemplate.getDisplayName() );
        assertEquals( "A social intranet for the Enterprise", siteTemplate.getInfo() );
        assertEquals( "[components/pages/main-page, components/news-part, components/my-layout]",
                      Iterators.toString( siteTemplate.resourcePathIterator() ) );
        assertEquals( layoutTemplate, siteTemplate.getTemplate( ResourcePath.from( "components/my-layout" ) ) );
        assertEquals( partTemplate, siteTemplate.getTemplate( new PartTemplateName( "news-part" ) ) );
    }
}
