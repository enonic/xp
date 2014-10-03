package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.context.Context;

public interface PageTemplateService
{
    PageTemplate create( CreatePageTemplateParams params, Context context );

    PageTemplate getByKey( PageTemplateKey pageTemplateKey, Context context );

    PageTemplate getDefault( GetDefaultPageTemplateParams params, Context context );

    PageTemplates getBySite( ContentId site, Context context );
}
