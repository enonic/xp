package com.enonic.wem.api.content.type;


import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.EntrySelector;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.formitem.Component;
import com.enonic.wem.api.content.type.formitem.FieldSet;
import com.enonic.wem.api.content.type.formitem.FormItem;
import com.enonic.wem.api.content.type.formitem.FormItemSet;

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
            if ( formItem instanceof Component )
            {
                processComponent( (Component) formItem, entrySelector );
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

    private void processComponent( final Component component, final EntrySelector entrySelector )
    {
        Data data = entrySelector != null ? entrySelector.getData( new EntryPath( component.getPath().toString() ) ) : null;
        if ( component.isRequired() )
        {
            verifyRequiredComponent( component, data );
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


    private void verifyRequiredComponent( final Component component, final Data data )
    {
        if ( data == null )
        {
            throw new BreaksRequiredContractException( component );
        }
        else
        {
            component.checkBreaksRequiredContract( data );
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
