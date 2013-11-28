package com.enonic.wem.api.command.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;

public class GetContentByPath
    extends Command<Content>
{
    private final ContentPath path;

    public GetContentByPath( final ContentPath path )
    {
        this.path = path;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( path, "path must be specified" );
    }

    public ContentPath getPath()
    {
        return this.path;
    }
}
