package com.enonic.wem.core.content;

import com.enonic.wem.api.content.BaseType;

public interface BaseTypeSerializer
{
    public String toString( BaseType type )
        throws SerializingException;
}
