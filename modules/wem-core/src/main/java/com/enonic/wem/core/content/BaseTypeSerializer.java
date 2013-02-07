package com.enonic.wem.core.content;

import com.enonic.wem.api.content.BaseType;
import com.enonic.wem.core.support.serializer.SerializingException;

public interface BaseTypeSerializer
{
    public String toString( BaseType type )
        throws SerializingException;
}
