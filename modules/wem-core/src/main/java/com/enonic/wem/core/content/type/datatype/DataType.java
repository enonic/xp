package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.data.Data;

public interface DataType
{
    boolean validData( final Data data );

    BasalValueType getBasalValueType();
}
