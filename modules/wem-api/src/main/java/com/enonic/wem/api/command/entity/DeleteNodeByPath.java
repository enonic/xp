package com.enonic.wem.api.command.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;

public class DeleteNodeByPath
    extends Command<Node>
{
    private final NodePath path;

    public DeleteNodeByPath( final NodePath path )
    {
        this.path = path;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( path, "path must be specified" );
    }

    public NodePath getPath()
    {
        return path;
    }
}
