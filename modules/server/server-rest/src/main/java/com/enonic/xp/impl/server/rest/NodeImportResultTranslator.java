package com.enonic.xp.impl.server.rest;

import java.time.Duration;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.LoadError;
import com.enonic.xp.export.NodeImportResult;

class NodeImportResultTranslator
{
    static BranchLoadResult translate( final NodeImportResult result, final Branch branch, final Duration duration )
    {
        return BranchLoadResult.create( branch ).
            successful( (long) result.addedNodes.getSize() ).
            errors( result.getImportErrors().stream().
                map( error -> LoadError.error( error.getMessage() ) ).
                collect( Collectors.toList() ) ).
            duration( duration ).
            build();
    }
}
