package com.enonic.wem.api.content.type.formitem.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.formitem.InvalidValueException;

public class GeoLocation
    extends BaseInputType
{
    public GeoLocation()
    {
        super( "geoLocation" );
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        DataTypes.GEOGRAPHIC_COORDINATE.checkValidity( data );
    }

    @Override
    public void ensureType( final Data data )
    {
        DataTypes.GEOGRAPHIC_COORDINATE.ensureType( data );
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
