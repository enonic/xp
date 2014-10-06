package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.FindContentByParentParams;
import com.enonic.wem.api.content.FindContentByParentResult;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplates;

final class GetPageTemplateBySiteCommand
{
    private ContentService contentService;

    private ContentId siteId;

    public PageTemplates execute()
    {
        final PageTemplates.Builder pageTemplatesBuilder = PageTemplates.newPageTemplates();
        final Content site = contentService.getById( siteId );
        final ContentPath pageTemplatesFolderPath = ContentPath.from( site.getPath(), "templates" );
        final FindContentByParentResult result =
            contentService.findByParent( FindContentByParentParams.create().parentPath( pageTemplatesFolderPath ).build() );
        for ( final Content content : result.getContents() )
        {
            if ( content instanceof PageTemplate )
            {
                pageTemplatesBuilder.add( (PageTemplate) content );
            }
        }
        return pageTemplatesBuilder.build();
    }

    public GetPageTemplateBySiteCommand site( final ContentId siteId )
    {
        this.siteId = siteId;
        return this;
    }

    public GetPageTemplateBySiteCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
