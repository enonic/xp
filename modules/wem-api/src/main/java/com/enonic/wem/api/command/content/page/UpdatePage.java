package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.PageEditor;

public class UpdatePage
    extends Command<Boolean>
{
    private ContentId content;

    private PageEditor editor;

    public UpdatePage content( final ContentId contentId )
    {
        this.content = contentId;
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

    public ContentId getContent()
    {
        return content;
    }

    public PageEditor getEditor()
    {
        return editor;
    }
}
