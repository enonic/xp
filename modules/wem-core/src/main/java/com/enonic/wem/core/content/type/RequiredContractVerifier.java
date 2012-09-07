package com.enonic.wem.core.content.type;


import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.data.DataSet;
import com.enonic.wem.core.content.data.EntryPath;
import com.enonic.wem.core.content.data.EntrySelector;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;
import com.enonic.wem.core.content.type.formitem.Component;
import com.enonic.wem.core.content.type.formitem.FormItem;
import com.enonic.wem.core.content.type.formitem.FormItemSet;
import com.enonic.wem.core.content.type.formitem.VisualFieldSet;

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
            else if ( formItem instanceof VisualFieldSet )
            {
                processFormItems( ( (VisualFieldSet) formItem ).getFormItemsIterable(), entrySelector );
            }
        }
    }

    private void processComponent( final Component component, final EntrySelector entrySelector )
    {
        Data data = entrySelector != null ? entrySelector.getData( new EntryPath( component.getPath().toString() ) ) : null;
        if ( component.isRequired() )
        {
            verifyRequiredField( component, data );
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


    private void verifyRequiredField( final Component component, final Data data )
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
