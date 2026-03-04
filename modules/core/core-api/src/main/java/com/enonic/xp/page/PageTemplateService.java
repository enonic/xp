package com.enonic.xp.page;

import com.enonic.xp.content.ContentId;


public interface PageTemplateService
{
    PageTemplate create( CreatePageTemplateParams params );

    PageTemplate getByKey( PageTemplateKey pageTemplateKey );

    PageTemplate getDefault( GetDefaultPageTemplateParams params );

    PageTemplates getBySite( ContentId site );
}
