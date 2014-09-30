package com.enonic.wem.api.workspace;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class Workspaces
    extends AbstractImmutableEntitySet<Workspace>
{
    private Workspaces( final ImmutableSet<Workspace> set )
    {
        super( set );
    }

    public static Workspaces from( final Workspace... workspaces )
    {
        return new Workspaces( ImmutableSet.copyOf( workspaces ) );
    }

}
