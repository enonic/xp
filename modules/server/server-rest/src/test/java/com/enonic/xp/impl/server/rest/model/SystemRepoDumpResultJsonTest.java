package com.enonic.xp.impl.server.rest.model;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpError;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.Assert.*;

public class SystemRepoDumpResultJsonTest
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

        System.out.println( this.mapper.writeValueAsString( json ) );

        assertEquals( 2, json.getRepositories().size() );
        assertEquals( 2, json.getRepositories().get( 0 ).getBranches().size() );
    }
}