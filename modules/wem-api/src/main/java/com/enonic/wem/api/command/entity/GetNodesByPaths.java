package com.enonic.wem.api.command.entity;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;

public class GetNodesByPaths
    extends Command<Nodes>
{
    private final NodePaths paths;

    public GetNodesByPaths( final NodePaths paths )
    {
        this.paths = paths;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( paths, "paths must be specified" );
    }

    public NodePaths getPaths()
    {
        return this.paths;
    }
}


