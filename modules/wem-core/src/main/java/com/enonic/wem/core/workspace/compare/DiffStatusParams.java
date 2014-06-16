package com.enonic.wem.core.workspace.compare;

import com.enonic.wem.core.version.VersionBranch;

class DiffStatusParams
{
    private final VersionBranch source;

    private final VersionBranch target;

    public DiffStatusParams( final VersionBranch source, final VersionBranch target )
    {
        this.source = source;
        this.target = target;
    }

    public VersionBranch getSource()
    {
        return source;
    }

    public VersionBranch getTarget()
    {
        return target;
    }
}
