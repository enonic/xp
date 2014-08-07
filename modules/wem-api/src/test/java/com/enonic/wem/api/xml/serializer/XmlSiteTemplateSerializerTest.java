package com.enonic.wem.api.xml.serializer;

import org.junit.Test;

import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.xml.mapper.XmlSiteTemplateMapper;
import com.enonic.wem.api.xml.model.XmlSiteTemplate;

import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static com.enonic.wem.api.schema.content.ContentTypeFilter.newContentFilter;
import static junit.framework.Assert.assertEquals;

public class XmlSiteTemplateSerializerTest
    extends BaseXmlSerializer2Test
{
    @Test
    public void test_to_xml()
        throws Exception
    {
        final ContentTypeFilter contentTypeFilter = newContentFilter().defaultDeny().
            allowContentTypes( ContentTypeNames.from( "com.enonic.tweet", "system.folder" ) ).
            denyContentType( "com.enonic.tweet.internal" ).
            build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "Intranet-1.0.0" ) ).
            displayName( "Enonic Intranet" ).
            description( "A social intranet for the Enterprise" ).
            url( "http://www.enonic.com/intranet" ).
            vendor( newVendor().name( "Enonic" ).url( "https://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                      "com.enonic.resolvers-1.0.0" ) ).
            contentTypeFilter( contentTypeFilter ).
            build();

        XmlSiteTemplate xmlObject = XmlSiteTemplateMapper.toXml( siteTemplate );
        String result = XmlSerializers2.siteTemplate().serialize( xmlObject );

        assertXml( "site-template.xml", result );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "site-template.xml" );
        final SiteTemplate.Builder builder = SiteTemplate.newSiteTemplate();

        XmlSiteTemplate xmlObject = XmlSerializers2.siteTemplate().parse( xml );
        XmlSiteTemplateMapper.fromXml( xmlObject, builder );
        SiteTemplate siteTemplate = builder.build();

        assertEquals( "Enonic Intranet", siteTemplate.getDisplayName() );
        assertEquals( "A social intranet for the Enterprise", siteTemplate.getDescription() );
        assertEquals( "http://www.enonic.com/intranet", siteTemplate.getUrl() );
        assertEquals( "Enonic", siteTemplate.getVendor().getName() );
        assertEquals( "https://www.enonic.com", siteTemplate.getVendor().getUrl() );
        assertEquals( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                       "com.enonic.resolvers-1.0.0" ), siteTemplate.getModules() );

        final ContentTypeFilter contentTypeFilter = newContentFilter().
            defaultDeny().
            allowContentTypes( ContentTypeNames.from( "com.enonic.tweet", "system.folder" ) ).
            denyContentType( "com.enonic.tweet.internal" ).
            build();
        assertEquals( contentTypeFilter, siteTemplate.getContentTypeFilter() );

    }

}
