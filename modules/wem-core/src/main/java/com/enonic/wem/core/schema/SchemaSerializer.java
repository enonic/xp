package com.enonic.wem.core.schema;

import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.core.support.serializer.SerializingException;

public interface SchemaSerializer
{
    public String toString( Schema type )
        throws SerializingException;
}
