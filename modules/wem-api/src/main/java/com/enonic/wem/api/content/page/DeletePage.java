package com.enonic.wem.api.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;

public class DeletePage
    extends Command<Content>
{
    private ContentId content;

    public DeletePage content( ContentId value )
    {
        this.content = value;
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
}
