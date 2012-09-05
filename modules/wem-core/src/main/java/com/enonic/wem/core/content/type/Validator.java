package com.enonic.wem.core.content.type;


import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.data.DataSet;
import com.enonic.wem.core.content.data.Entry;
import com.enonic.wem.core.content.data.InvalidDataException;
import com.enonic.wem.core.content.type.configitem.Field;

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
        data.checkValidity();

        Field field = contentType.getField( data.getPath().resolveConfigItemPath() );
        field.checkValidity( data );
    }

    private void doValidate( Iterable<Entry> entries )
    {
        for ( Entry entry : entries )
        {
            if ( entry instanceof Data )
            {
                validate( (Data) entry );
            }
            else if ( entry instanceof DataSet )
            {
                doValidate( (DataSet) entry );
            }
        }
    }
}
