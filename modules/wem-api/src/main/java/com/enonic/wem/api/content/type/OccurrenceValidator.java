package com.enonic.wem.api.content.type;


import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.EntrySelector;
import com.enonic.wem.api.content.type.component.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.component.Component;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.FieldSet;
import com.enonic.wem.api.content.type.component.Input;

public final class OccurrenceValidator
{
    private final ContentType contentType;

    public OccurrenceValidator( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public void verify( final ContentData contentData )
    {
        processComponents( contentType.componentIterable(), contentData );
    }

    private void processComponents( final Iterable<Component> components, final EntrySelector entrySelector )
    {
        // check missing required entries
        for ( Component component : components )
        {
            if ( component instanceof Input )
            {
                processInput( (Input) component, entrySelector );
            }
            else if ( component instanceof ComponentSet )
            {
                processComponentSet( (ComponentSet) component, entrySelector );
            }
            else if ( component instanceof FieldSet )
            {
                processComponents( ( (FieldSet) component ).componentIterable(), entrySelector );
            }
        }
    }

    private void processInput( final Input input, final EntrySelector entrySelector )
    {
        final Data data = getData( input, entrySelector );
        if ( input.isRequired() )
        {
            verifyRequiredInput( input, data );
        }
    }

    private void processComponentSet( final ComponentSet componentSet, final EntrySelector entrySelector )
    {
        final DataSet dataSet = getDataSet( componentSet, entrySelector );
        if ( componentSet.isRequired() )
        {
            verifyRequiredComponentSet( componentSet, dataSet );
        }

        if ( dataSet != null )
        {
            processComponents( componentSet.getComponents().iterable(), dataSet );
        }
        else
        {
            processComponents( componentSet.getComponents().iterable(), null );
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

    private void verifyRequiredComponentSet( final ComponentSet componentSet, final DataSet dataSet )
    {
        if ( dataSet == null )
        {
            throw new BreaksRequiredContractException( componentSet );
        }
    }

    private static Data getData( final Input input, final EntrySelector entrySelector )
    {
        return entrySelector != null ? entrySelector.getData( new EntryPath( input.getPath().toString() ) ) : null;
    }

    private DataSet getDataSet( final ComponentSet componentSet, final EntrySelector entrySelector )
    {
        return entrySelector != null ? entrySelector.getDataSet( new EntryPath( componentSet.getPath().toString() ) ) : null;
    }

}
