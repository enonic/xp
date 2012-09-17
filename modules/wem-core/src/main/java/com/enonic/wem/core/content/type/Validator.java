package com.enonic.wem.core.content.type;


import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.data.DataSet;
import com.enonic.wem.core.content.data.InvalidDataException;
import com.enonic.wem.core.content.type.formitem.Component;
import com.enonic.wem.core.content.type.formitem.FormItem;
import com.enonic.wem.core.content.type.formitem.FormItemSet;

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

    private void doValidate( Iterable<Data> entries )
    {
        for ( Data data : entries )
        {
            doValidate( data );
        }
    }

    private void doValidate( final Data data )
        throws InvalidDataException
    {
        if ( !data.isDataSet() )
        {
            data.checkValidity();

            final FormItem formItem = contentType.getFormItem( data.getPath().resolveFormItemPath().toString() );
            if ( formItem != null )
            {
                if ( formItem instanceof Component )
                {
                    final Component component = (Component) formItem;
                    component.checkValidity( data );
                }
            }
        }
        else
        {
            doValidateDataWithDataSet( data );
        }
    }

    private void doValidateDataWithDataSet( final Data dataWithDataSet )
    {
        final DataSet dataSet = dataWithDataSet.getDataSet();
        final FormItem formItem = contentType.getFormItem( dataSet.getPath().resolveFormItemPath().toString() );
        if ( formItem != null )
        {
            if ( formItem instanceof FormItemSet )
            {
                final FormItemSet formItemSet = (FormItemSet) formItem;
                for ( Data subData : dataSet )
                {
                    final FormItem subFormItem = formItemSet.getFormItem( subData.getPath().resolveFormItemPath() );
                    if ( subFormItem instanceof Component )
                    {
                        final Component component = (Component) subFormItem;
                        component.checkValidity( subData );
                    }
                }
            }
            else if ( formItem instanceof Component )
            {
                final Component component = (Component) formItem;
                component.checkValidity( dataWithDataSet );
            }
        }
        else
        {
            doValidate( dataSet );
        }
    }


}
