package com.enonic.wem.core.content.type;


import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.data.DataSet;
import com.enonic.wem.core.content.data.EntryPath;
import com.enonic.wem.core.content.data.EntrySelector;
import com.enonic.wem.core.content.type.configitem.BreaksRequiredContractException;
import com.enonic.wem.core.content.type.configitem.Component;
import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.VisualFieldSet;

public class RequiredContractVerifier
{
    private ContentType contentType;

    public RequiredContractVerifier( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public void verify( final ContentData contentData )
    {
        processConfigItems( contentType.getConfigItems().getIterable(), contentData );
    }

    private void processConfigItems( final Iterable<ConfigItem> configItems, final EntrySelector entrySelector )
    {
        // check missing required entries
        for ( ConfigItem configItem : configItems )
        {
            if ( configItem instanceof Component )
            {
                processField( (Component) configItem, entrySelector );
            }
            else if ( configItem instanceof FieldSet )
            {
                processFieldSet( (FieldSet) configItem, entrySelector );
            }
            else if ( configItem instanceof VisualFieldSet )
            {
                processConfigItems( ( (VisualFieldSet) configItem ).getConfigItemsIterable(), entrySelector );
            }
        }
    }

    private void processField( final Component component, final EntrySelector entrySelector )
    {
        Data data = entrySelector != null ? entrySelector.getData( new EntryPath( component.getPath().toString() ) ) : null;
        if ( component.isRequired() )
        {
            verifyRequiredField( component, data );
        }
    }

    private void processFieldSet( final FieldSet fieldSet, final EntrySelector entrySelector )
    {
        DataSet dataSet = entrySelector != null ? entrySelector.getDataSet( new EntryPath( fieldSet.getPath().toString() ) ) : null;
        if ( fieldSet.isRequired() )
        {
            verifyRequiredFieldSet( fieldSet, dataSet );
        }

        if ( dataSet != null )
        {
            processConfigItems( fieldSet.getConfigItems().iterable(), dataSet );
        }
        else
        {
            processConfigItems( fieldSet.getConfigItems().iterable(), null );
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

    private void verifyRequiredFieldSet( final FieldSet fieldSet, final DataSet dataSet )
    {
        if ( dataSet == null )
        {
            throw new BreaksRequiredContractException( fieldSet );
        }
    }

}
