package com.enonic.wem.core.content.type.valuetype;


import com.enonic.wem.core.content.data.Value;

public class MultiLinedString
    extends BaseValueType
    implements ValueType
{
    public boolean validValue( final Value fieldValue )
    {
        return fieldValue.getValue() instanceof String;
    }
}
