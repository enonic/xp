package com.enonic.wem.core.content.type.configitem.fieldtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.configitem.BreaksRequiredContractException;

public class WholeNumber
    extends BaseFieldType
{
    WholeNumber()
    {
        super( "wholeNumber", DataTypes.WHOLE_NUMBER );
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
