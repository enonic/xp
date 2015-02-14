package com.enonic.xp.core.impl.content.page;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentId;
import com.enonic.xp.core.content.ContentPath;
import com.enonic.xp.core.content.ContentService;
import com.enonic.xp.core.content.FindContentByParentParams;
import com.enonic.xp.core.content.FindContentByParentResult;
import com.enonic.xp.core.content.page.PageTemplate;
import com.enonic.xp.core.content.page.PageTemplates;
import com.enonic.xp.core.impl.content.ContentServiceImpl;

final class GetPageTemplateBySiteCommand
{
    private ContentService contentService;

    private ContentId siteId;

    public PageTemplates execute()
    {
        final PageTemplates.Builder pageTemplatesBuilder = PageTemplates.newPageTemplates();
        final Content site = contentService.getById( siteId );
        final ContentPath pageTemplatesFolderPath = ContentPath.from( site.getPath(), ContentServiceImpl.TEMPLATES_FOLDER_NAME );
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
