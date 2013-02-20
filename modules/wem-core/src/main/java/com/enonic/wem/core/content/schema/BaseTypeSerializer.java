package com.enonic.wem.core.content.schema;

import com.enonic.wem.api.content.schema.BaseType;
import com.enonic.wem.core.support.serializer.SerializingException;

public interface BaseTypeSerializer
{
    public String toString( BaseType type )
        throws SerializingException;
}
