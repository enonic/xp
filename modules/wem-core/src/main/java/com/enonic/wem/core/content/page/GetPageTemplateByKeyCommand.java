package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.context.Context;

final class GetPageTemplateByKeyCommand
{
    private Context context;

    private ContentService contentService;

    private PageTemplateKey pageTemplateKey;

    public PageTemplate execute()
    {
        return (PageTemplate) this.contentService.getById( pageTemplateKey, context );
    }

    public GetPageTemplateByKeyCommand pageTemplateKey( final PageTemplateKey pageTemplateKey )
    {
        this.pageTemplateKey = pageTemplateKey;
        return this;
    }

    public GetPageTemplateByKeyCommand context( final Context context )
    {
        this.context = context;
        return this;
    }

    public GetPageTemplateByKeyCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
