package com.enonic.wem.core.content.type.valuetype;


import com.enonic.wem.core.content.data.Value;

public interface ValueType
{
    boolean validValue( final Value fieldValue );

    BasalValueType getBasalValueType();
}
