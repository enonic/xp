package com.enonic.wem.api.content.site;

import org.junit.Test;

import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static com.enonic.wem.api.schema.content.ContentTypeFilter.newContentFilter;
import static org.junit.Assert.*;

public class SiteTemplateTest
{

    @Test
    public void siteTemplate()
    {
        final ContentTypeFilter contentTypeFilter =
            newContentFilter().defaultDeny().allowContentTypes( ContentTypeNames.from( "mymodule-1.0.0:com.enonic.intranet", "mymodule-1.0.0:system.folder" ) ).build();
        SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "Intranet-1.0.0" ) ).
            displayName( "Enonic Intranet" ).
            description( "A social intranet for the Enterprise" ).
            vendor( newVendor().name( "Enonic" ).url( "https://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                      "com.enonic.resolvers-1.0.0" ) ).
            contentTypeFilter( contentTypeFilter ).
            build();

        assertEquals( SiteTemplateKey.from( "Intranet-1.0.0" ), siteTemplate.getKey() );
        assertEquals( new SiteTemplateName( "Intranet" ), siteTemplate.getName() );
        assertEquals( new SiteTemplateVersion( "1.0.0" ), siteTemplate.getVersion() );
        assertEquals( "Enonic Intranet", siteTemplate.getDisplayName() );
        assertEquals( "A social intranet for the Enterprise", siteTemplate.getDescription() );
        assertEquals( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                       "com.enonic.resolvers-1.0.0" ), siteTemplate.getModules() );
        assertEquals( contentTypeFilter, siteTemplate.getContentTypeFilter() );
        assertEquals( "Enonic", siteTemplate.getVendor().getName() );
        assertEquals( "https://www.enonic.com", siteTemplate.getVendor().getUrl() );
    }

    @Test
    public void siteTemplateWithResources()
    {
        final RootDataSet partTemplateConfig = new RootDataSet();
        partTemplateConfig.addProperty( "width", Value.newLong( 200 ) );

        final RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", Value.newLong( 10000 ) );

        final PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "mainmodule.0|main-page" ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "mymodule-1.0.0:article", "mymodule-1.0.0:banner" ) ).
            descriptor( PageDescriptorKey.from( "mainmodule-1.0.0:landing-page" ) ).
            build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "Intranet-1.0.0" ) ).
            displayName( "Enonic Intranet" ).
            description( "A social intranet for the Enterprise" ).
            addPageTemplate( pageTemplate ).
            build();

        assertEquals( SiteTemplateKey.from( "Intranet-1.0.0" ), siteTemplate.getKey() );
        assertEquals( new SiteTemplateName( "Intranet" ), siteTemplate.getName() );
        assertEquals( new SiteTemplateVersion( "1.0.0" ), siteTemplate.getVersion() );
        assertEquals( "Enonic Intranet", siteTemplate.getDisplayName() );
        assertEquals( "A social intranet for the Enterprise", siteTemplate.getDescription() );
    }
}
