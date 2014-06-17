package com.enonic.wem.core.workspace.compare;

import com.enonic.wem.api.entity.CompareStatus;
import com.enonic.wem.core.version.VersionBranch;
import com.enonic.wem.core.version.VersionEntry;

class DiffStatusResolver
{
    public static CompareStatus resolve( final DiffStatusParams diffStatusParams )
    {
        final VersionBranch targetBranch = diffStatusParams.getTarget();
        final VersionBranch sourceBranch = diffStatusParams.getSource();

        if ( targetBranch.isEmpty() )
        {
            return new CompareStatus( CompareStatus.State.NEW );
        }

        if ( sourceBranch.isEmpty() )
        {
            return new CompareStatus( CompareStatus.State.DELETED );
        }

        final VersionEntry currentInSource = sourceBranch.getFirst();
        final VersionEntry currentInTarget = targetBranch.getFirst();

        if ( currentInSource.equals( currentInTarget ) )
        {
            return new CompareStatus( CompareStatus.State.EQUAL );
        }

        if ( sourceBranch.has( currentInTarget ) )
        {
            return new CompareStatus( CompareStatus.State.NEWER );
        }

        if ( targetBranch.has( currentInSource ) )
        {
            return new CompareStatus( CompareStatus.State.OLDER );
        }

        return new CompareStatus( CompareStatus.State.CONFLICT );
    }

}
