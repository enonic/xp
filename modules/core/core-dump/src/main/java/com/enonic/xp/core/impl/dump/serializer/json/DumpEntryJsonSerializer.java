package com.enonic.xp.core.impl.dump.serializer.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.core.impl.dump.model.DumpEntry;
import com.enonic.xp.core.impl.dump.serializer.DumpEntrySerializer;

public class DumpEntryJsonSerializer
    implements DumpEntrySerializer
{
    private final static ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String serialize( final DumpEntry dumpEntry )
    {
        try
        {
            return MAPPER.writeValueAsString( new DumpEntryJson( dumpEntry ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( "Cannot serializer dumpEntry" );
        }

    }
}
