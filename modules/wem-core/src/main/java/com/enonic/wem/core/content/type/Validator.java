package com.enonic.wem.core.content.type;


import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.data.DataSet;
import com.enonic.wem.core.content.data.InvalidDataException;
import com.enonic.wem.core.content.type.formitem.Component;

/**
 * Validates that given data is valid, meaning it is of valid:
 * type, format, value.
 */
public class Validator
{
    private ContentType contentType;

    public Validator( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public void validate( ContentData contentData )
        throws InvalidDataException
    {
        doValidate( contentData );
    }

    public void validate( DataSet dataSet )
        throws InvalidDataException
    {
        doValidate( dataSet );
    }

    public void validate( Data data )
        throws InvalidDataException
    {
        if ( !data.isDataSet() )
        {
            data.checkValidity();
            Component component = contentType.getComponent( data.getPath().resolveFormItemPath() );
            component.checkValidity( data );
        }
        else
        {
            final DataSet dataSet = (DataSet) data.getValue();
            doValidate( dataSet );
        }
    }

    private void doValidate( Iterable<Data> entries )
    {
        for ( Data data : entries )
        {
            validate( data );
        }
    }
}
