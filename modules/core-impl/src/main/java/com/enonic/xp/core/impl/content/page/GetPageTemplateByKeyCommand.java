package com.enonic.xp.core.impl.content.page;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;

final class GetPageTemplateByKeyCommand
{
    private ContentService contentService;

    private PageTemplateKey pageTemplateKey;

    public PageTemplate execute()
    {
        return (PageTemplate) this.contentService.getById( pageTemplateKey );
    }

    public GetPageTemplateByKeyCommand pageTemplateKey( final PageTemplateKey pageTemplateKey )
    {
        this.pageTemplateKey = pageTemplateKey;
        return this;
    }

    public GetPageTemplateByKeyCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
