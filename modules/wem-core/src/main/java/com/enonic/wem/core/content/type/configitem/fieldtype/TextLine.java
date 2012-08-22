package com.enonic.wem.core.content.type.configitem.fieldtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.datatype.DataTypes;

public class TextLine
    extends BaseFieldType
{
    TextLine()
    {
        super( "textLine", DataTypes.SINGLE_LINED_STRING );
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
        String stringValue = (String) data.getValue();
        return StringUtils.isBlank( stringValue );
    }
}
