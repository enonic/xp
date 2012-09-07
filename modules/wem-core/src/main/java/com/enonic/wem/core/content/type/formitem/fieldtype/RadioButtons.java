package com.enonic.wem.core.content.type.formitem.fieldtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;

public class RadioButtons
    extends BaseComponentType
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

    public AbstractComponentTypeConfigSerializerJson getComponentTypeConfigJsonGenerator()
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
            throw new BreaksRequiredContractException( data, this );
        }
    }
}
