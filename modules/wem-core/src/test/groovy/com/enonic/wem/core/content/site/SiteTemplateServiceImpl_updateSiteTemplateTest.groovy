package com.enonic.wem.core.content.site

import com.enonic.wem.api.content.page.PageDescriptorKey
import com.enonic.wem.api.content.page.PageTemplate
import com.enonic.wem.api.content.page.PageTemplateKey
import com.enonic.wem.api.content.site.*
import com.enonic.wem.api.module.ModuleKeys
import com.enonic.wem.api.schema.content.ContentTypeName

import static com.enonic.wem.api.schema.content.ContentTypeFilter.newContentFilter

class SiteTemplateServiceImpl_updateSiteTemplateTest
    extends AbstractSiteTemplateServiceTest
{
    def "update site template"()
    {
        given:
        def template = createSiteTemplate();
        def editor = SetSiteTemplateEditor.newEditor().description( "new description" ).build();
        def createSiteTemplateParam = new UpdateSiteTemplateParams().
            key( SiteTemplateKey.from( "mysitetemplate-1.0.0" ) ).
            editor( editor );

        when:
        def result = this.service.updateSiteTemplate( createSiteTemplateParam );

        then:
        result != null;
        result != template;
    }

    def createSiteTemplate()
    {
        def vendor = Vendor.newVendor().name( "Enonic" ).url( "http://enonic.net" ).build();
        def moduleKeys = ModuleKeys.from( "foomodule-1.0.0" );
        def filter = newContentFilter().defaultDeny().allowContentType( ContentTypeName.from( "page" ) ).build();
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
            rootContentType( ContentTypeName.from( "document" ) ).
            addPageTemplate( pageTemplate );
        createSiteTemplate( siteTemplate );
        return siteTemplate;
    }
}
