package com.enonic.wem.core.content.site

import com.enonic.wem.api.content.site.CreateSiteTemplateParams
import com.enonic.wem.api.content.site.SiteTemplateVersion
import com.enonic.wem.api.content.site.Vendor
import com.enonic.wem.api.module.ModuleKeys
import com.enonic.wem.api.schema.content.ContentTypeName

import static com.enonic.wem.api.schema.content.ContentTypeFilter.newContentFilter

class SiteTemplateServiceImpl_getSiteTemplatesTest
    extends AbstractSiteTemplateServiceTest
{
    def "get site templates"()
    {
        given:
        createSiteTemplate( "intranet" );
        createSiteTemplate( "other" );

        when:
        def result = this.service.getSiteTemplates();

        then:
        result != null;
        !result.isEmpty();
        result.getSize() == 2
    }

    def createSiteTemplate( String name )
    {
        def vendor = Vendor.newVendor().name( "Enonic" ).url( "http://enonic.net" ).build();
        def moduleKeys = ModuleKeys.from( "foomodule-1.0.0" );
        def filter = newContentFilter().defaultDeny().allowContentType( ContentTypeName.from( "mymodule-1.0.0:page" ) ).build();
        def createSiteTemplateParam = new CreateSiteTemplateParams().
            name( name ).
            version( SiteTemplateVersion.from( "1.2.0" ) ).
            displayName( "Intranet template" ).
            vendor( vendor ).
            url( "http://www.enonic.com" ).
            modules( moduleKeys ).
            description( "description" ).
            contentTypeFilter( filter );

        this.service.siteTemplateExporter = new SiteTemplateExporter();
        this.service.createSiteTemplate( createSiteTemplateParam );
    }
}
