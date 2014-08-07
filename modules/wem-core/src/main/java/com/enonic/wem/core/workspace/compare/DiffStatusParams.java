package com.enonic.wem.core.workspace.compare;

import com.enonic.wem.api.entity.EntityVersion;

class DiffStatusParams
{
    private final EntityVersion source;

    private final EntityVersion target;

    public DiffStatusParams( final EntityVersion source, final EntityVersion target )
    {
        this.source = source;
        this.target = target;
    }

    public EntityVersion getSource()
    {
        return source;
    }

    public EntityVersion getTarget()
    {
        return target;
    }
}
