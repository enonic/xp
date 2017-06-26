package com.enonic.xp.impl.server.rest;

import java.time.Duration;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.export.NodeImportResult;

class NodeImportResultTranslator
{
    static BranchLoadResult translate( final NodeImportResult result, final Branch branch, final Duration duration )
    {
        return BranchLoadResult.create( branch ).
            addedNodes( (long) result.addedNodes.getSize() ).
            addedVersions( (long) result.addedNodes.getSize() ).
            duration( duration ).
            build();
    }
}
