package com.enonic.wem.core.content.type.valuetype;


import com.enonic.wem.core.content.data.Value;

public interface ValueType
{
    public boolean validValue( final Value fieldValue );
}
