package com.enonic.xp.repo.impl.dump.serializer.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;

public class DumpMetaJsonSerializer
{
    private final static ObjectMapper MAPPER = new ObjectMapper();

    public String serialize( final DumpMeta dumpMeta )
    {
        try
        {
            return MAPPER.writeValueAsString( DumpMetaJson.from( dumpMeta ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serializer dumpEntry", e );
        }
    }

}
