package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.support.serializer.ParsingException;
import com.enonic.wem.core.support.serializer.SerializingException;

public interface MixinSerializer
{
    public String toString( Mixin mixin )
        throws SerializingException;

    public Mixin toMixin( String serialized )
        throws ParsingException;
}
