package com.enonic.wem.core.content.type.configitem.fieldtype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.datatype.DataTypes;

public class Date
    extends BaseFieldType
{
    Date()
    {
        super( "date", DataTypes.DATE );
    }

    public FieldTypeJsonGenerator getJsonGenerator()
    {
        return BaseFieldTypeJsonGenerator.DEFAULT;
    }

    @Override
    public boolean validData( final Data data )
    {
        return getDataType().validData( data );
    }

    public boolean requiresConfig()
    {
        return false;
    }

    public Class requiredConfigClass()
    {
        return null;
    }

    @Override
    public boolean breaksRequiredContract( final Data data )
    {
        // TODO
        return false;
    }
}

