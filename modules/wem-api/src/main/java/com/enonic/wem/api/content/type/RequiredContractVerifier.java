package com.enonic.wem.api.content.type;


import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.EntrySelector;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.formitem.FieldSet;
import com.enonic.wem.api.content.type.formitem.FormItem;
import com.enonic.wem.api.content.type.formitem.FormItemSet;
import com.enonic.wem.api.content.type.formitem.Input;

public class RequiredContractVerifier
{
    private ContentType contentType;

    public RequiredContractVerifier( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public void verify( final ContentData contentData )
    {
        processFormItems( contentType.getFormItems().getIterable(), contentData );
    }

    private void processFormItems( final Iterable<FormItem> formItems, final EntrySelector entrySelector )
    {
        // check missing required entries
        for ( FormItem formItem : formItems )
        {
            if ( formItem instanceof Input )
            {
                processInput( (Input) formItem, entrySelector );
            }
            else if ( formItem instanceof FormItemSet )
            {
                processFormItemSet( (FormItemSet) formItem, entrySelector );
            }
            else if ( formItem instanceof FieldSet )
            {
                processFormItems( ( (FieldSet) formItem ).getFormItemsIterable(), entrySelector );
            }
        }
    }

    private void processInput( final Input input, final EntrySelector entrySelector )
    {
        Data data = entrySelector != null ? entrySelector.getData( new EntryPath( input.getPath().toString() ) ) : null;
        if ( input.isRequired() )
        {
            verifyRequiredInput( input, data );
        }
    }

    private void processFormItemSet( final FormItemSet formItemSet, final EntrySelector entrySelector )
    {
        DataSet dataSet = entrySelector != null ? entrySelector.getDataSet( new EntryPath( formItemSet.getPath().toString() ) ) : null;
        if ( formItemSet.isRequired() )
        {
            verifyRequiredFormItemSet( formItemSet, dataSet );
        }

        if ( dataSet != null )
        {
            processFormItems( formItemSet.getFormItems().iterable(), dataSet );
        }
        else
        {
            processFormItems( formItemSet.getFormItems().iterable(), null );
        }
    }


    private void verifyRequiredInput( final Input input, final Data data )
    {
        if ( data == null )
        {
            throw new BreaksRequiredContractException( input );
        }
        else
        {
            input.checkBreaksRequiredContract( data );
        }
    }

    private void verifyRequiredFormItemSet( final FormItemSet formItemSet, final DataSet dataSet )
    {
        if ( dataSet == null )
        {
            throw new BreaksRequiredContractException( formItemSet );
        }
    }

}
