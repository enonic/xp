package com.enonic.wem.api.content.type.formitem.comptype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.datatype.DataTool;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.formitem.InvalidValueException;

public class Image
    extends BaseComponentType
{
    public Image()
    {
        super( "image" );
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        DataTool.checkDataType( data, "binary", DataTypes.BLOB );
        DataTool.checkDataType( data, "caption", DataTypes.TEXT );
    }

    @Override
    public void ensureType( final Data data )
    {
        final DataSet dataSet = data.getDataSet();
        DataTypes.BLOB.ensureType( dataSet.getData( "binary" ) );
        DataTypes.TEXT.ensureType( dataSet.getData( "caption" ) );
    }

    @Override
    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {
        final DataSet dataSet = data.getDataSet();
        final Data binary = dataSet.getData( "binary" );
        if ( binary == null )
        {
            throw new BreaksRequiredContractException( data, this );
        }

        if ( !binary.hasValue() )
        {
            throw new BreaksRequiredContractException( data, this );
        }


    }
}

