package com.enonic.wem.api.command.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;

public class GetContentById
    extends Command<Content>
{
    private final ContentId id;

    public GetContentById( final ContentId id )
    {
        this.id = id;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( id, "id must be specified" );
    }

    public ContentId getId()
    {
        return this.id;
    }
}
