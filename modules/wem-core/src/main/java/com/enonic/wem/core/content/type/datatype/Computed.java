package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class Computed
    extends AbstractDataType
    implements DataType
{
    public Computed()
    {
        super( null, FieldTypes.VIRTUAL );
    }

    public boolean validData( final Data data )
    {
        return true;
    }
}
