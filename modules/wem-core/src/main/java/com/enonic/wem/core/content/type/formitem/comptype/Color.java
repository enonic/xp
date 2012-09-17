package com.enonic.wem.core.content.type.formitem.comptype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.data.DataSet;
import com.enonic.wem.core.content.data.InvalidDataException;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;

public class Color
    extends BaseComponentType
{
    Color()
    {
        super( "color", DataTypes.DATA_SET, TypedPath.newTypedPath( "red", DataTypes.WHOLE_NUMBER ),
               TypedPath.newTypedPath( "green", DataTypes.WHOLE_NUMBER ), TypedPath.newTypedPath( "blue", DataTypes.WHOLE_NUMBER ) );
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
            throw new BreaksRequiredContractException( data, this );
        }
    }

    @Override
    public void checkValidity( final Data data )
    {
        super.checkValidity( data );

        DataSet dataSet = data.getDataSet();
        Data red = dataSet.getData( "red" );
        Data green = dataSet.getData( "green" );
        Data blue = dataSet.getData( "blue" );

        verify( red, "red" );
        verify( green, "green" );
        verify( blue, "blue" );
    }

    private void verify( final Data data, final String path )
    {
        if ( data == null || data.getValue() == null )
        {
            throw new InvalidDataException( data, "Not a Color without " + path );
        }
        final Long value = (Long) data.getValue();
        if ( value < 0 || value > 255 )
        {
            throw new InvalidDataException( data, "[" + data.getPath() + "] must be between 0 and 255" );
        }
    }
}
