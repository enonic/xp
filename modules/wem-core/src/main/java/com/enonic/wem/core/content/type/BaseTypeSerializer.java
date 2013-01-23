package com.enonic.wem.core.content.type;

import com.enonic.wem.api.content.type.BaseType;
import com.enonic.wem.core.content.SerializingException;

public interface BaseTypeSerializer
{
    public String toString( BaseType type )
        throws SerializingException;
}
