package com.enonic.xp.repo.impl.dump.serializer.json;

import java.io.IOException;

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

    public DumpMeta toDumpMeta( final String value )
    {
        try
        {
            final DumpMetaJson dumpMetaJson = MAPPER.readValue( value, DumpMetaJson.class );
            return DumpMetaJson.fromJson( dumpMetaJson );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value [" + value + "] to DumpMeta", e );
        }
    }

}
