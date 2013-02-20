package com.enonic.wem.core.content.schema;

import com.enonic.wem.api.content.schema.Schema;
import com.enonic.wem.core.support.serializer.SerializingException;

public interface SchemaSerializer
{
    public String toString( Schema type )
        throws SerializingException;
}
