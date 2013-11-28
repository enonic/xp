package com.enonic.wem.api.command.content.site;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;

public class DeleteSite
    extends Command<Content>
{
    private ContentId content;

    public DeleteSite content( final ContentId value )
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
