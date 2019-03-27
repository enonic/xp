package com.enonic.xp.repo.impl.dump.serializer.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repo.impl.dump.serializer.DumpSerializer;

public class JsonDumpSerializer
    implements DumpSerializer
{
    private final static ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String serialize( final BranchDumpEntry branchDumpEntry )
    {
        try
        {
            return MAPPER.writeValueAsString( BranchDumpEntryJson.from( branchDumpEntry ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serializer dumpEntry", e );
        }
    }

    @Override
    public String serialize( final VersionsDumpEntry versionsDumpEntry )
    {
        try
        {
            return MAPPER.writeValueAsString( VersionsDumpEntryJson.from( versionsDumpEntry ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serializer dumpEntry", e );
        }
    }

    @Override
    public String serialize( final CommitDumpEntry commitDumpEntry )
    {
        try
        {
            return MAPPER.writeValueAsString( CommitDumpEntryJson.from( commitDumpEntry ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serializer dumpEntry", e );
        }
    }

    @Override
    public BranchDumpEntry toBranchMetaEntry( final String value )
    {
        try
        {
            final BranchDumpEntryJson branchDumpEntryJson = MAPPER.readValue( value, BranchDumpEntryJson.class );
            return BranchDumpEntryJson.fromJson( branchDumpEntryJson );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value [" + value + "] to DumpEntry", e );
        }
    }

    @Override
    public VersionsDumpEntry toNodeVersionsEntry( final String value )
    {
        try
        {
            final VersionsDumpEntryJson nodeVersionMetaEntryJson = MAPPER.readValue( value, VersionsDumpEntryJson.class );
            return VersionsDumpEntryJson.fromJson( nodeVersionMetaEntryJson );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value [" + value + "] to DumpEntry", e );
        }
    }

    @Override
    public CommitDumpEntry toCommitDumpEntry( final String value )
    {
        try
        {
            final CommitDumpEntryJson commitDumpEntryJson = MAPPER.readValue( value, CommitDumpEntryJson.class );
            return CommitDumpEntryJson.fromJson( commitDumpEntryJson );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value [" + value + "] to DumpEntry", e );
        }
    }
}
