package com.enonic.wem.core.workspace.compare;

import com.enonic.wem.api.entity.CompareState;
import com.enonic.wem.core.version.VersionBranch;
import com.enonic.wem.core.version.VersionEntry;

class DiffStatusResolver
{
    public static CompareState resolve( final DiffStatusParams diffStatusParams )
    {
        final VersionBranch targetBranch = diffStatusParams.getTarget();
        final VersionBranch sourceBranch = diffStatusParams.getSource();

        if ( targetBranch.isEmpty() )
        {
            return new CompareState( CompareState.State.NEW );
        }

        if ( sourceBranch.isEmpty() )
        {
            return new CompareState( CompareState.State.DELETED );
        }

        final VersionEntry currentInSource = sourceBranch.getFirst();
        final VersionEntry currentInTarget = targetBranch.getFirst();

        if ( currentInSource.equals( currentInTarget ) )
        {
            return new CompareState( CompareState.State.EQUAL );
        }

        if ( sourceBranch.has( currentInTarget ) )
        {
            return new CompareState( CompareState.State.NEWER );
        }

        if ( targetBranch.has( currentInSource ) )
        {
            return new CompareState( CompareState.State.OLDER );
        }

        return new CompareState( CompareState.State.CONFLICT );
    }

}
