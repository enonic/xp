package com.enonic.wem.core.content.type.valuetype;


import com.enonic.wem.core.content.data.Value;

public class Xml
    extends AbstractValueType
    implements ValueType
{
    public Xml()
    {
        super( BasalValueType.STRING );
    }

    public boolean validValue( final Value fieldValue )
    {
        return true;
    }
}
