package com.enonic.wem.core.workspace.compare;

import com.enonic.wem.api.entity.NodeVersion;

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
