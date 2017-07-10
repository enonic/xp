package com.enonic.xp.impl.server.rest.model;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.LoadError;
import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.Assert.*;

public class SystemRepoLoadResultJsonTest
{
    private ObjectMapper mapper;

    @Before
    public void setUp()
        throws Exception
    {
        this.mapper = new ObjectMapper();
        this.mapper.enable( SerializationFeature.INDENT_OUTPUT );
        this.mapper.enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );
        this.mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );

    }

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

        System.out.println( mapper.writeValueAsString( json ) );

        assertEquals( 2, json.getRepositories().size() );
        assertEquals( 2, json.getRepositories().get( 0 ).getBranches().size() );
    }
}