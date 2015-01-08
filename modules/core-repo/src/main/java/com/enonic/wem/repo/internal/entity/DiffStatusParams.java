package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.node.NodeVersion;

class DiffStatusParams
{
    private final NodeVersion source;

    private final NodeVersion target;

    public DiffStatusParams( final NodeVersion source, final NodeVersion target )
    {
        this.source = source;
        this.target = target;
    }

    public NodeVersion getSource()
    {
        return source;
    }

    public NodeVersion getTarget()
    {
        return target;
    }
}
