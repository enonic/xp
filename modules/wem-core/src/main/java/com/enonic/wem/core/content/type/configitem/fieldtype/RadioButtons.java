package com.enonic.wem.core.content.type.configitem.fieldtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.configitem.BreaksRequiredContractException;

public class RadioButtons
    extends BaseFieldType
{
    RadioButtons()
    {
        super( "radioButtons", DataTypes.STRING );
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
    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {
        final String stringValue = (String) data.getValue();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( data );
        }
    }
}
