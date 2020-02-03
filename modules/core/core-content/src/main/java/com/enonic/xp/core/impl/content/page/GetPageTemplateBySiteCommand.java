package com.enonic.xp.core.impl.content.page;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.core.impl.content.ContentServiceImpl;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplates;

final class GetPageTemplateBySiteCommand
{
    private ContentService contentService;

    private ContentId siteId;

    private ContentPath sitePath;

    public PageTemplates execute()
    {
        final PageTemplates.Builder pageTemplatesBuilder = PageTemplates.create();
        if ( sitePath == null )
        {
            final Content site = contentService.getById( siteId );
            sitePath = site.getPath();
        }
        final ContentPath pageTemplatesFolderPath = ContentPath.from( sitePath, ContentServiceImpl.TEMPLATES_FOLDER_NAME );
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

    public GetPageTemplateBySiteCommand sitePath( final ContentPath sitePath )
    {
        this.sitePath = sitePath;
        return this;
    }

    public GetPageTemplateBySiteCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
