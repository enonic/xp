package com.enonic.wem.core.content.type;

import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.core.content.ParsingException;
import com.enonic.wem.core.content.SerializingException;

public interface SubTypeSerializer
{
    public String toString( SubType subType )
        throws SerializingException;

    public SubType toSubType( String xml )
        throws ParsingException;
}
