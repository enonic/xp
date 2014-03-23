package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.schema.content.ContentTypeName;

public interface PageTemplateService
{
    PageTemplate create( final CreatePageTemplateParams params );

    boolean delete( final DeletePageTemplateParams params );

    PageTemplate getByKey( final PageTemplateKey pageTemplateKey, final SiteTemplateKey siteTemplateKey );

    PageTemplate getDefault( final SiteTemplateKey siteTemplateKey, final ContentTypeName contentType );

    PageTemplates getBySiteTemplate( final SiteTemplateKey siteTemplateKey );
}
