package com.enonic.wem.core.content.site

import com.enonic.wem.api.content.page.PageDescriptorKey
import com.enonic.wem.api.content.page.PageTemplate
import com.enonic.wem.api.content.page.PageTemplateKey
import com.enonic.wem.api.content.site.CreateSiteTemplateParams
import com.enonic.wem.api.content.site.SiteTemplateName
import com.enonic.wem.api.content.site.Vendor
import com.enonic.wem.api.module.ModuleKeys
import com.enonic.wem.api.schema.content.ContentTypeName

import static com.enonic.wem.api.schema.content.ContentTypeFilter.newContentFilter

class SiteTemplateServiceImpl_createSiteTemplateTest
    extends AbstractSiteTemplateServiceTest
{
    def "create site template"()
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

        when:
        def result = this.service.createSiteTemplate( createSiteTemplateParam );

        then:
        result != null;
        result.getName() == new SiteTemplateName( "intranet" );
        result.getDisplayName() == "Intranet template";
        result.getVendor() == vendor;
        result.getUrl() == "http://www.enonic.com";
        result.getModules() == moduleKeys;
        result.getDescription() == "description";
        result.getContentTypeFilter() == filter;
    }

    def "create site template with page template"()
    {
        given:
        def vendor = Vendor.newVendor().name( "Enonic" ).url( "http://enonic.net" ).build();
        def moduleKeys = ModuleKeys.from( "foomodule-1.0.0" );
        def filter = newContentFilter().defaultDeny().allowContentType( ContentTypeName.from( "mymodule-1.0.0:page" ) ).build();
        def pageTemplate = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "template-name" ) ).
            displayName( "My page template" ).
            descriptor( PageDescriptorKey.from( "resource-1.0.0:page-descr" ) ).build();

        def createSiteTemplateParam = new CreateSiteTemplateParams().
            name( "intranet" ).
            displayName( "Intranet template" ).
            vendor( vendor ).
            url( "http://www.enonic.com" ).
            modules( moduleKeys ).
            description( "description" ).
            contentTypeFilter( filter ).
            addPageTemplate( pageTemplate );

        when:
        def result = this.service.createSiteTemplate( createSiteTemplateParam );

        then:
        result != null;
        result.getName() == new SiteTemplateName( "intranet" );
        result.getDisplayName() == "Intranet template";
        result.getVendor() == vendor;
        result.getUrl() == "http://www.enonic.com";
        result.getModules() == moduleKeys;
        result.getDescription() == "description";
        result.getContentTypeFilter() == filter;
        result.getPageTemplates().getSize() == 1;
        result.getPageTemplates().first().getDisplayName() == "My page template";
    }
}
