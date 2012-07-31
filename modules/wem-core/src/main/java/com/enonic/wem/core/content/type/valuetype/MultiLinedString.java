package com.enonic.wem.core.content.type.valuetype;


import com.enonic.wem.core.content.data.Value;

public class MultiLinedString
    extends AbstractValueType
    implements ValueType
{
    public MultiLinedString()
    {
        super( BasalValueType.STRING );
    }

    public boolean validValue( final Value fieldValue )
    {
        return fieldValue.getValue() instanceof String;
    }
}
