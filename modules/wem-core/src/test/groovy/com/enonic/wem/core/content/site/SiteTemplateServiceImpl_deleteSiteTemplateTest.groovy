package com.enonic.wem.core.content.site

import com.enonic.wem.api.content.page.PageDescriptorKey
import com.enonic.wem.api.content.page.PageTemplate
import com.enonic.wem.api.content.page.PageTemplateKey
import com.enonic.wem.api.content.site.SiteTemplate
import com.enonic.wem.api.content.site.SiteTemplateKey
import com.enonic.wem.api.content.site.Vendor
import com.enonic.wem.api.module.ModuleKeys
import com.enonic.wem.api.schema.content.ContentTypeName

import java.nio.file.Files

import static com.enonic.wem.api.schema.content.ContentTypeFilter.newContentFilter

class SiteTemplateServiceImpl_deleteSiteTemplateTest
    extends AbstractSiteTemplateServiceTest
{
    def "delete site template"()
    {
        given:
        def template = createSiteTemplate().build();
        assert Files.exists( this.templatesDir.resolve( template.getKey().toString() ) );

        when:
        this.service.deleteSiteTemplate( template.getKey() );

        then:
        !Files.exists( this.templatesDir.resolve( template.getKey().toString() ) );
    }

    def createSiteTemplate()
    {
        def vendor = Vendor.newVendor().name( "Enonic" ).url( "http://enonic.net" ).build();
        def moduleKeys = ModuleKeys.from( "foomodule-1.0.0" );
        def filter = newContentFilter().defaultDeny().allowContentType( ContentTypeName.from( "mymodule-1.0.0:page" ) ).build();
        def pageTemplate = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "foomodule-1.0.0|template-name" ) ).
            displayName( "My page template" ).
            descriptor( PageDescriptorKey.from( "resource-1.0.0:page-descr" ) ).build();

        def siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "mysitetemplate-1.0.0" ) ).
            displayName( "Intranet template" ).
            vendor( vendor ).
            url( "http://www.enonic.com" ).
            modules( moduleKeys ).
            description( "description" ).
            contentTypeFilter( filter ).
            addPageTemplate( pageTemplate );
        createSiteTemplate( siteTemplate );
        return siteTemplate;
    }
}
