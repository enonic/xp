package com.enonic.xp.repo.impl.dump.serializer.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repo.impl.dump.serializer.DumpEntrySerializer;

public class DumpEntryJsonSerializer
    implements DumpEntrySerializer
{
    private final static ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String serialize( final DumpEntry dumpEntry )
    {
        try
        {
            return MAPPER.writeValueAsString( DumpEntryJson.from( dumpEntry ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serializer dumpEntry", e );
        }
    }

    @Override
    public DumpEntry deSerialize( final String value )
    {
        try
        {
            final DumpEntryJson dumpEntryJson = MAPPER.readValue( value, DumpEntryJson.class );
            return DumpEntryJson.fromJson( dumpEntryJson );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value [" + value + "] to DumpEntry", e );
        }


    }

}
