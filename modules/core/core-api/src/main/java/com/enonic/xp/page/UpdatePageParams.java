package com.enonic.xp.page;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;

@PublicApi
public final class UpdatePageParams
{
    private ContentId content;

    private PageEditor editor;

    public UpdatePageParams content( final ContentId contentId )
    {
        this.content = contentId;
        return this;
    }

    public UpdatePageParams editor( final PageEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public ContentId getContent()
    {
        return content;
    }

    public PageEditor getEditor()
    {
        return editor;
    }
}
