package com.enonic.wem.api.content.type.formitem.comptype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;

public class Dropdown
    extends BaseComponentType
{
    public Dropdown()
    {
        super( "dropdown", DataTypes.TEXT );
    }

    public boolean requiresConfig()
    {
        return true;
    }

    public Class requiredConfigClass()
    {
        return DropdownConfig.class;
    }

    public AbstractComponentTypeConfigSerializerJson getComponentTypeConfigJsonGenerator()
    {
        return DropdownConfigSerializerJson.DEFAULT;
    }

    @Override
    public AbstractComponentTypeConfigSerializerXml getComponentTypeConfigXmlGenerator()
    {
        return DropdownConfigSerializerXml.DEFAULT;
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

