package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;

public class UpdatePage
    extends Command<Boolean>
{
    private ContentId contentId;

    private PageEditor editor;

    public UpdatePage contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public UpdatePage editor( final PageEditor editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
