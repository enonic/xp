package com.enonic.wem.core.workspace.compare;

import com.enonic.wem.core.version.VersionEntry;

class DiffStatusParams
{
    private final VersionEntry source;

    private final VersionEntry target;

    public DiffStatusParams( final VersionEntry source, final VersionEntry target )
    {
        this.source = source;
        this.target = target;
    }

    public VersionEntry getSource()
    {
        return source;
    }

    public VersionEntry getTarget()
    {
        return target;
    }
}
