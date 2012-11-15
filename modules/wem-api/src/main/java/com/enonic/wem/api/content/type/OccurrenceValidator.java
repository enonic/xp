package com.enonic.wem.api.content.type;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;

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
import com.enonic.wem.api.content.type.component.MaximumOccurrencesException;
import com.enonic.wem.api.content.type.component.MinimumOccurrencesException;

public final class OccurrenceValidator
{
    private final ContentType contentType;

    private boolean recordExceptions = false;

    private List<RuntimeException> recordedExceptions;

    private OccurrenceValidator( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public void validate( final ContentData contentData )
    {
        recordedExceptions = new ArrayList<RuntimeException>();

        processComponents( contentType.componentIterable(), contentData );
        processData( contentData );
    }

    public Iterator<RuntimeException> getRecordedExceptions()
    {
        return recordedExceptions.iterator();
    }

    private void processData( final Iterable<Data> datas )
    {
        for ( final Data data : datas )
        {
            final Component component = contentType.getComponent( data.getPath().resolveComponentPath() );

            if ( component instanceof Input )
            {
                try
                {
                    processData( data, (Input) component );
                }
                catch ( MaximumOccurrencesException e )
                {
                    handleException( e );
                }
            }
            else if ( component instanceof ComponentSet )
            {
                try
                {
                    processComponentSet( data, (ComponentSet) component );
                }
                catch ( MaximumOccurrencesException e )
                {
                    handleException( e );
                }
            }
        }
    }

    private void processComponentSet( final Data data, final ComponentSet set )
        throws MaximumOccurrencesException
    {
        if ( set.getOccurrences().getMaximum() > 0 && data.hasArrayAsValue() )
        {
            if ( data.getDataArray().size() > set.getOccurrences().getMaximum() )
            {
                throw new MaximumOccurrencesException( set, data.getDataArray().size() );
            }
        }
    }

    private void processData( final Data data, final Input input )
        throws MaximumOccurrencesException
    {
        if ( input.getOccurrences().getMaximum() > 0 && data.hasArrayAsValue() )
        {
            if ( data.getDataArray().size() > input.getOccurrences().getMaximum() )
            {
                throw new MaximumOccurrencesException( input, data.getDataArray().size() );
            }
        }
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
            try
            {
                verifyRequiredInput( input, data );
            }
            catch ( MinimumOccurrencesException e )
            {
                handleException( e );
            }
        }
    }

    private void processComponentSet( final ComponentSet componentSet, final EntrySelector entrySelector )
    {
        final DataSet dataSet = getDataSet( componentSet, entrySelector );
        if ( componentSet.isRequired() )
        {
            try
            {
                verifyRequiredComponentSet( componentSet, dataSet );
            }
            catch ( MinimumOccurrencesException e )
            {
                handleException( e );
            }
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
            throw new MinimumOccurrencesException( input, 0 );
        }
        else
        {
            input.checkBreaksMinimumOccurrencesContract( data );
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

    private void handleException( final RuntimeException e )
    {
        if ( recordExceptions )
        {
            recordedExceptions.add( e );
        }
        else
        {
            throw e;
        }
    }

    public static Builder newOccurrenceValidator()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentType contentType;

        private boolean recordExceptions;

        public Builder contentType( ContentType contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder recordExceptions( boolean value )
        {
            this.recordExceptions = value;
            return this;
        }

        public OccurrenceValidator build()
        {
            Preconditions.checkNotNull( this.contentType, "contenType is required" );
            OccurrenceValidator validator = new OccurrenceValidator( this.contentType );
            validator.recordExceptions = this.recordExceptions;
            return validator;
        }

    }

}
