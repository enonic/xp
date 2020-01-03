package com.enonic.xp.impl.server.rest.model;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.LoadError;
import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SystemRepoLoadResultJsonTest
{
    @Test
    public void create()
        throws Exception
    {
        final SystemLoadResult results = SystemLoadResult.create().
            add( RepoLoadResult.create( RepositoryId.from( "my-repo" ) ).
                add( BranchLoadResult.create( Branch.from( "myBranch" ) ).
                    successful( 100L ).
                    build() ).
                add( BranchLoadResult.create( Branch.from( "myOtherBranch" ) ).
                    successful( 100L ).
                    build() ).
                build() ).
            add( RepoLoadResult.create( RepositoryId.from( "my-other-repo" ) ).
                add( BranchLoadResult.create( Branch.from( "myBranch2" ) ).
                    successful( 100L ).
                    error( LoadError.error( "this is an error" ) ).
                    error( LoadError.error( "this is another error" ) ).
                    build() ).
                versions( VersionsLoadResult.create().
                    successful( 10L ).
                    error( LoadError.error( "fisk" ) ).
                    build() ).
                build() ).
            build();

        final SystemLoadResultJson json = SystemLoadResultJson.from( results );

        assertEquals( 2, json.getRepositories().size() );
        assertEquals( 2, json.getRepositories().get( 0 ).getBranches().size() );
    }
}
