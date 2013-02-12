package com.enonic.wem.core.content.mixin;

import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.core.support.serializer.ParsingException;
import com.enonic.wem.core.support.serializer.SerializingException;

public interface MixinSerializer
{
    public String toString( Mixin mixin )
        throws SerializingException;

    public Mixin toMixin( String serialized )
        throws ParsingException;
}
