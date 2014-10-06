package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.ContentId;

public interface PageTemplateService
{
    PageTemplate create( CreatePageTemplateParams params );

    PageTemplate getByKey( PageTemplateKey pageTemplateKey );

    PageTemplate getDefault( GetDefaultPageTemplateParams params );

    PageTemplates getBySite( ContentId site );
}
