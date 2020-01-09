package com.enonic.xp.impl.server.rest.model;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpError;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SystemRepoDumpResultJsonTest
{
    @Test
    public void create()
        throws Exception
    {
        final SystemDumpResult systemDumpResult = SystemDumpResult.create().
            add( RepoDumpResult.create( RepositoryId.from( "repo1" ) ).
                add( BranchDumpResult.create( Branch.from( "branch1" ) ).
                    addedNodes( 5 ).
                    build() ).
                add( BranchDumpResult.create( Branch.from( "branch2" ) ).
                    addedNodes( 23 ).
                    build() ).
                addedVersion().
                build() ).
            add( RepoDumpResult.create( RepositoryId.from( "repo2" ) ).
                add( BranchDumpResult.create( Branch.from( "branch3" ) ).
                    addedNodes( 5 ).
                    build() ).
                add( BranchDumpResult.create( Branch.from( "branch4" ) ).
                    addedNodes( 23 ).
                    error( DumpError.error( "cannot find binary with version 123" ) ).
                    error( DumpError.error( "cannot find version with id 123" ) ).
                    build() ).
                addedVersion().
                build() ).
            build();

        final SystemDumpResultJson json = SystemDumpResultJson.from( systemDumpResult );

        assertEquals( 2, json.getRepositories().size() );
        assertEquals( 2, json.getRepositories().get( 0 ).getBranches().size() );
    }
}
