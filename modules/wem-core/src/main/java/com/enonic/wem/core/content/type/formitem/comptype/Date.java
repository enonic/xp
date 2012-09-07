package com.enonic.wem.core.content.type.formitem.comptype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;

public class Date
    extends BaseComponentType
{
    Date()
    {
        super( "date", DataTypes.DATE );
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
        if ( data.getValue() == null )
        {
            throw new BreaksRequiredContractException( data, this );
        }
    }
}

