package com.enonic.xp.core.content.page;

import com.enonic.xp.core.content.ContentId;

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
