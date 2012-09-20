package com.enonic.wem.core.content.type.formitem.comptype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;

public class Address
    extends BaseComponentType
{
    Address()
    {
        super( "address", DataTypes.DATA_SET, TypedPath.newTypedPath( "street", DataTypes.TEXT ),
               TypedPath.newTypedPath( "postalCode", DataTypes.TEXT ), TypedPath.newTypedPath( "postalPlace", DataTypes.TEXT ),
               TypedPath.newTypedPath( "region", DataTypes.TEXT ), TypedPath.newTypedPath( "country", DataTypes.TEXT ) );
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
        if ( !data.hasValue() )
        {
            throw new BreaksRequiredContractException( data, this );
        }
    }
}

