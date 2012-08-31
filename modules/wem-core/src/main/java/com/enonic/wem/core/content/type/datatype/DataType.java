package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldType;

public interface DataType
{
    boolean validData( final Data data );

    public FieldType getDefaultFieldType();

    BasalValueType getBasalValueType();
}
