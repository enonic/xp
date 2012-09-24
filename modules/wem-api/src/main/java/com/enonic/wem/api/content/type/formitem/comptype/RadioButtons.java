package com.enonic.wem.api.content.type.formitem.comptype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;

public class RadioButtons
    extends BaseComponentType
{
    RadioButtons()
    {
        super( "radioButtons", DataTypes.TEXT );
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
    public AbstractComponentTypeConfigSerializerXml getComponentTypeConfigXmlGenerator()
    {
        return RadioButtonsConfigSerializerXml.DEFAULT;
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
