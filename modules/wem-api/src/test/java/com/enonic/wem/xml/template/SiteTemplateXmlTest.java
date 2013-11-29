package com.enonic.wem.xml.template;

import org.junit.Test;

import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.content.site.ContentTypeFilter.newContentFilter;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static junit.framework.Assert.assertEquals;

public class SiteTemplateXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        final ContentTypeFilter contentTypeFilter =
            newContentFilter().defaultDeny().allowContentTypes( ContentTypeNames.from( "com.enonic.tweet", "system.folder" ) ).build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "Intranet-1.0.0" ) ).
            displayName( "Enonic Intranet" ).
            description( "A social intranet for the Enterprise" ).
            url( "http://www.enonic.com/intranet" ).
            vendor( newVendor().name( "Enonic" ).url( "https://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                      "com.enonic.resolvers-1.0.0" ) ).
            contentTypeFilter( contentTypeFilter ).
            rootContentType( ContentTypeName.from( "com.enonic.intranet" ) ).
            build();

        final SiteTemplateXml siteTemplateXml = new SiteTemplateXml();
        siteTemplateXml.from( siteTemplate );
        final String result = XmlSerializers.siteTemplate().serialize( siteTemplateXml );

        assertXml( "site-template.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "site-template.xml" );
        final SiteTemplate.Builder builder = SiteTemplate.newSiteTemplate();

        XmlSerializers.siteTemplate().parse( xml ).to( builder );

        final SiteTemplate siteTemplate = builder.build();

        assertEquals( "Enonic Intranet", siteTemplate.getDisplayName() );
        assertEquals( "A social intranet for the Enterprise", siteTemplate.getDescription() );
        assertEquals( "http://www.enonic.com/intranet", siteTemplate.getUrl() );
        assertEquals( "Enonic", siteTemplate.getVendor().getName() );
        assertEquals( "https://www.enonic.com", siteTemplate.getVendor().getUrl() );
        assertEquals( ContentTypeName.from( "com.enonic.intranet" ), siteTemplate.getRootContentType() );
        assertEquals( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                       "com.enonic.resolvers-1.0.0" ), siteTemplate.getModules() );

        final ContentTypeFilter contentTypeFilter =
            newContentFilter().defaultDeny().allowContentTypes( ContentTypeNames.from( "com.enonic.tweet", "system.folder" ) ).build();
        assertEquals( contentTypeFilter, siteTemplate.getContentTypeFilter() );
    }
}
