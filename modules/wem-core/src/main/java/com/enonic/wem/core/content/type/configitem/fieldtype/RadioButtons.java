package com.enonic.wem.core.content.type.configitem.fieldtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.datatype.DataTypes;

public class RadioButtons
    extends BaseFieldType
{
    RadioButtons()
    {
        super( "radioButtons", DataTypes.SINGLE_LINED_STRING );
    }

    @Override
    public boolean validData( final Data data )
    {
        return getDataType().validData( data );
    }

    public boolean requiresConfig()
    {
        return true;
    }

    public Class requiredConfigClass()
    {
        return RadioButtonsConfig.class;
    }

    public AbstractFieldTypeConfigSerializerJson getFieldTypeConfigJsonGenerator()
    {
        return RadioButtonsConfigSerializerJson.DEFAULT;
    }

    @Override
    public boolean breaksRequiredContract( final Data data )
    {
        String stringValue = (String) data.getValue();
        return StringUtils.isBlank( stringValue );
    }
}
