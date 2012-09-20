package com.enonic.wem.core.content.type.formitem.comptype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;

import static com.enonic.wem.core.content.type.formitem.comptype.TypedPath.newTypedPath;

public class Money
    extends BaseComponentType
{
    Money()
    {
        super( "money", DataTypes.DATA_SET, newTypedPath( "amount", DataTypes.DECIMAL_NUMBER ),
               newTypedPath( "currency", DataTypes.TEXT ) );
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
    {
        final String stringValue = (String) data.getValue();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( data, this );
        }
    }
}

