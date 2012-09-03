package com.enonic.wem.core.content.type.configitem.fieldtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.BreaksRequiredContractException;
import com.enonic.wem.core.content.type.datatype.DataTypes;

public class Dropdown
    extends BaseFieldType
{
    Dropdown()
    {
        super( "dropdown", DataTypes.STRING );
    }

    public boolean requiresConfig()
    {
        return true;
    }

    public Class requiredConfigClass()
    {
        return DropdownConfig.class;
    }

    public AbstractFieldTypeConfigSerializerJson getFieldTypeConfigJsonGenerator()
    {
        return DropdownConfigSerializerJson.DEFAULT;
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

