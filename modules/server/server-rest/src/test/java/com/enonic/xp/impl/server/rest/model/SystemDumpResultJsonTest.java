package com.enonic.xp.impl.server.rest.model;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpResult;
import com.enonic.xp.dump.DumpResults;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.Assert.*;

public class SystemDumpResultJsonTest
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
        final DumpResults dumpResults = DumpResults.create().
            add( DumpResult.create( RepositoryId.from( "repo1" ) ).
                add( BranchDumpResult.create( Branch.from( "branch1" ) ).
                    addedVersions( 10 ).
                    addedNodes( 5 ).
                    build() ).
                add( BranchDumpResult.create( Branch.from( "branch2" ) ).
                    addedVersions( 109 ).
                    addedNodes( 23 ).
                    build() ).
                build() ).
            add( DumpResult.create( RepositoryId.from( "repo2" ) ).
                add( BranchDumpResult.create( Branch.from( "branch3" ) ).
                    addedVersions( 10 ).
                    addedNodes( 5 ).
                    build() ).
                add( BranchDumpResult.create( Branch.from( "branch4" ) ).
                    addedVersions( 109 ).
                    addedNodes( 23 ).
                    build() ).
                build() ).
            build();

        final SystemDumpResultJson json = SystemDumpResultJson.from( dumpResults );

        assertEquals( 2, json.getRepositories().size() );
        assertEquals( 2, json.getRepositories().get( 0 ).getBranches().size() );
    }
}