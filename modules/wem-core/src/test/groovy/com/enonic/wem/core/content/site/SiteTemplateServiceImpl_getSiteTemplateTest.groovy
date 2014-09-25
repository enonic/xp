package com.enonic.wem.core.content.site

import com.enonic.wem.api.content.site.CreateSiteTemplateParams
import com.enonic.wem.api.content.site.SiteTemplateKey
import com.enonic.wem.api.content.site.Vendor
import com.enonic.wem.api.module.ModuleKeys
import com.enonic.wem.api.schema.content.ContentTypeName

import static com.enonic.wem.api.schema.content.ContentTypeFilter.newContentFilter

class SiteTemplateServiceImpl_getSiteTemplateTest
    extends AbstractSiteTemplateServiceTest
{
    def "get site template"()
    {
        given:
        def vendor = Vendor.newVendor().name( "Enonic" ).url( "http://enonic.net" ).build();
        def moduleKeys = ModuleKeys.from( "foomodule-1.0.0" );
        def filter = newContentFilter().defaultDeny().allowContentType( ContentTypeName.from( "mymodule-1.0.0:page" ) ).build();
        def createSiteTemplateParam = new CreateSiteTemplateParams().
                name( "intranet" ).
                displayName( "Intranet template" ).
                vendor( vendor ).
                url( "http://www.enonic.com" ).
                modules( moduleKeys ).
                description( "description" ).
                contentTypeFilter( filter );

        this.service.siteTemplateExporter = new SiteTemplateExporter();
        def existingSiteTemplate = this.service.createSiteTemplate( createSiteTemplateParam );

        when:
        def result = this.service.getSiteTemplate( SiteTemplateKey.from( "intranet" ) );

        then:
        result != null;
        result.getName() == existingSiteTemplate.getName(  );
        result.getDisplayName() == existingSiteTemplate.getDisplayName(  );
        result.getVendor() == existingSiteTemplate.getVendor(  );
        result.getUrl() == existingSiteTemplate.getUrl(  );
        result.getModules() == existingSiteTemplate.getModules(  );
        result.getDescription() == existingSiteTemplate.getDescription(  );
        result.getContentTypeFilter() == existingSiteTemplate.getContentTypeFilter(  );
    }

}
