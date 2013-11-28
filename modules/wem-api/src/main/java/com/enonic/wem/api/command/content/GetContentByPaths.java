package com.enonic.wem.api.command.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;

public class GetContentByPaths
    extends Command<Contents>
{
    private final ContentPaths paths;

    public GetContentByPaths( final ContentPaths paths )
    {
        this.paths = paths;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( paths, "paths must be specified" );
    }

    public ContentPaths getPaths()
    {
        return this.paths;
    }
}
