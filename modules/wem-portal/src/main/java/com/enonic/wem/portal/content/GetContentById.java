package com.enonic.wem.portal.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.script.command.Command;
import com.enonic.wem.script.command.CommandName;

@CommandName("content.getById")
public final class GetContentById
    extends Command<Content>
{
    private String id;

    public String getId()
    {
        return id;
    }

    public void setId( final String id )
    {
        this.id = id;
    }
}
