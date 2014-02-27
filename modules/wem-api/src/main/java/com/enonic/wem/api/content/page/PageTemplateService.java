package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.site.SiteTemplateKey;

public interface PageTemplateService
{
    PageTemplate create( final CreatePageTemplate command );

    boolean delete( final DeletePageTemplate command );

    PageTemplate getByKey( final PageTemplateKey pageTemplateKey, final SiteTemplateKey siteTemplateKey );

    PageTemplates getBySiteTemplate( final SiteTemplateKey siteTemplateKey );
}
