package com.enonic.xp.lib.content;

import com.enonic.xp.content.ContentService;

public final class ContentLibService
{
    private ContentService contentService;

    public GetContentHandler newGetContent()
    {
        return new GetContentHandler( this.contentService );
    }

    public GetChildContentHandler newGetContentChildren()
    {
        return new GetChildContentHandler( this.contentService );
    }

    public QueryContentHandler newQueryContent()
    {
        return new QueryContentHandler( this.contentService );
    }

    public DeleteContentHandler newDeleteContent()
    {
        return new DeleteContentHandler( this.contentService );
    }

    public CreateContentHandler newCreateContent()
    {
        return new CreateContentHandler( this.contentService );
    }

    public ModifyContentHandler newModifyContent()
    {
        return new ModifyContentHandler( this.contentService );
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
