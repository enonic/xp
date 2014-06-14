package com.enonic.wem.core.workspace.diff;

import com.enonic.wem.core.version.VersionBranch;
import com.enonic.wem.core.version.VersionEntry;

class DiffStatusResolver
{
    public static DiffStatus resolve( final DiffStatusParams diffStatusParams )
    {
        final VersionBranch targetBranch = diffStatusParams.getTarget();
        final VersionBranch sourceBranch = diffStatusParams.getSource();

        if ( targetBranch.isEmpty() )
        {
            return new DiffStatus( DiffStatus.State.NEW );
        }

        if ( sourceBranch.isEmpty() )
        {
            return new DiffStatus( DiffStatus.State.DELETED );
        }

        final VersionEntry currentInSource = sourceBranch.getFirst();
        final VersionEntry currentInTarget = targetBranch.getFirst();

        if ( currentInSource.equals( currentInTarget ) )
        {
            return new DiffStatus( DiffStatus.State.EQUAL );
        }

        if ( sourceBranch.has( currentInTarget ) )
        {
            return new DiffStatus( DiffStatus.State.NEWER );
        }

        if ( targetBranch.has( currentInSource ) )
        {
            return new DiffStatus( DiffStatus.State.OLDER );
        }

        return new DiffStatus( DiffStatus.State.CONFLICT );
    }

}
