package com.enonic.xp.core.impl.content.page;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;

final class GetPageTemplateByKeyCommand
{
    private ContentService contentService;

    private PageTemplateKey pageTemplateKey;

    public PageTemplate execute()
    {
        return (PageTemplate) this.contentService.getById( pageTemplateKey.getContentId() );
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
