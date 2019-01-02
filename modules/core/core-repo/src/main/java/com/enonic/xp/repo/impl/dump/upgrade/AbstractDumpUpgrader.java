package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.repo.impl.dump.RepoDumpException;

public abstract class AbstractDumpUpgrader
    implements DumpUpgrader
{
    protected final ObjectMapper mapper = ObjectMapperHelper.create();

    protected <T> T deserializeValue( final String value, final Class<T> clazz )
    {
        try
        {
            return mapper.readValue( value, clazz );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot deserialize value [" + value + "]", e );
        }
    }
}
