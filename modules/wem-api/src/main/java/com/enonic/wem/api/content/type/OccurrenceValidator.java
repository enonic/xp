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
import com.enonic.wem.api.content.type.form.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.MaximumOccurrencesException;
import com.enonic.wem.api.content.type.form.MinimumOccurrencesException;

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

        processFormItems( contentType.formItemIterable(), contentData );
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
            final FormItem formItem = contentType.getFormItem( data.getPath().resolveFormItemPath() );

            if ( formItem instanceof Input )
            {
                try
                {
                    processData( data, (Input) formItem );
                }
                catch ( MaximumOccurrencesException e )
                {
                    handleException( e );
                }
            }
            else if ( formItem instanceof FormItemSet )
            {
                try
                {
                    processSet( data, (FormItemSet) formItem );
                }
                catch ( MaximumOccurrencesException e )
                {
                    handleException( e );
                }
            }
        }
    }

    private void processSet( final Data data, final FormItemSet set )
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
                processSet( (FormItemSet) formItem, entrySelector );
            }
            else if ( formItem instanceof FieldSet )
            {
                processFormItems( ( (FieldSet) formItem ).formItemIterable(), entrySelector );
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

    private void processSet( final FormItemSet formItemSet, final EntrySelector entrySelector )
    {
        final DataSet dataSet = getDataSet( formItemSet, entrySelector );
        if ( formItemSet.isRequired() )
        {
            try
            {
                verifyRequiredFormItemSet( formItemSet, dataSet );
            }
            catch ( MinimumOccurrencesException e )
            {
                handleException( e );
            }
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
            throw new MinimumOccurrencesException( input, 0 );
        }
        else
        {
            input.checkBreaksMinimumOccurrencesContract( data );
        }
    }

    private void verifyRequiredFormItemSet( final FormItemSet formItemSet, final DataSet dataSet )
    {
        if ( dataSet == null )
        {
            throw new BreaksRequiredContractException( formItemSet );
        }
    }

    private static Data getData( final Input input, final EntrySelector entrySelector )
    {
        return entrySelector != null ? entrySelector.getData( new EntryPath( input.getPath().toString() ) ) : null;
    }

    private DataSet getDataSet( final FormItemSet formItemSet, final EntrySelector entrySelector )
    {
        return entrySelector != null ? entrySelector.getDataSet( new EntryPath( formItemSet.getPath().toString() ) ) : null;
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
