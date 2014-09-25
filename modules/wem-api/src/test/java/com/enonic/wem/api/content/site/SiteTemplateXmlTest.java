package com.enonic.wem.api.content.site;

import org.junit.Test;

import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.xml.BaseXmlSerializerTest;
import com.enonic.wem.api.xml.XmlSerializers;

import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static com.enonic.wem.api.schema.content.ContentTypeFilter.newContentFilter;
import static junit.framework.Assert.assertEquals;

public class SiteTemplateXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        final ContentTypeFilter contentTypeFilter =
            newContentFilter().defaultDeny().allowContentTypes( ContentTypeNames.from( "mymodule:com.enonic.tweet", "mymodule:system.folder" ) ).build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "Intranet-1.0.0" ) ).
            displayName( "Enonic Intranet" ).
            description( "A social intranet for the Enterprise" ).
            url( "http://www.enonic.com/intranet" ).
            vendor( newVendor().name( "Enonic" ).url( "https://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "com.enonic.intranet", "com.company.sampleModule", "com.company.theme.someTheme",
                                      "com.enonic.resolvers" ) ).
            contentTypeFilter( contentTypeFilter ).
            build();

        final SiteTemplateXml siteTemplateXml = new SiteTemplateXml();
        siteTemplateXml.from( siteTemplate );
        final String result = XmlSerializers.siteTemplate().serialize( siteTemplateXml );

        assertXml( "site.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "site.xml" );
        final SiteTemplate.Builder builder = SiteTemplate.newSiteTemplate();

        XmlSerializers.siteTemplate().parse( xml ).to( builder );

        final SiteTemplate siteTemplate = builder.build();

        assertEquals( "Enonic Intranet", siteTemplate.getDisplayName() );
        assertEquals( "A social intranet for the Enterprise", siteTemplate.getDescription() );
        assertEquals( "http://www.enonic.com/intranet", siteTemplate.getUrl() );
        assertEquals( "Enonic", siteTemplate.getVendor().getName() );
        assertEquals( "https://www.enonic.com", siteTemplate.getVendor().getUrl() );
        assertEquals( ModuleKeys.from( "com.enonic.intranet", "com.company.sampleModule", "com.company.theme.someTheme",
                                       "com.enonic.resolvers" ), siteTemplate.getModules() );

        final ContentTypeFilter contentTypeFilter =
            newContentFilter().defaultDeny().allowContentTypes( ContentTypeNames.from( "mymodule:com.enonic.tweet", "mymodule:system.folder" ) ).build();
        assertEquals( contentTypeFilter, siteTemplate.getContentTypeFilter() );
    }
}
