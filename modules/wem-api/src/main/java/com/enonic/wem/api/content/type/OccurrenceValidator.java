package com.enonic.wem.api.content.type;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataArray;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.EntrySelector;
import com.enonic.wem.api.content.type.form.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.Input;

public final class OccurrenceValidator
{
    private final ContentType contentType;

    public OccurrenceValidator( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public DataValidationErrors validate( final ContentData contentData )
    {
        final List<DataValidationError> validationErrors = Lists.newArrayList();

        validateFormItems( contentType.formItemIterable(), contentData, validationErrors );
        validateData( contentData, validationErrors );

        return DataValidationErrors.from( validationErrors );
    }

    private void validateData( final Iterable<Data> datas, final List<DataValidationError> validationErrors )
    {
        for ( final Data data : datas )
        {
            final FormItem formItem = contentType.getFormItem( data.getPath().resolveFormItemPath() );

            if ( formItem instanceof Input )
            {
                validateData( data, (Input) formItem, validationErrors );
            }
            else if ( formItem instanceof FormItemSet )
            {
                validateSet( data, (FormItemSet) formItem, validationErrors );
            }
        }
    }

    private void validateSet( final Data data, final FormItemSet set, final List<DataValidationError> validationErrors )
    {
        final int maxOccurrences = set.getOccurrences().getMaximum();
        if ( maxOccurrences > 0 && data.hasArrayAsValue() )
        {
            final int size = data.getDataArray().size();
            if ( size > maxOccurrences )
            {
                validationErrors.add( new MaximumOccurrencesValidationError( set, size ) );
            }
        }
    }

    private void validateData( final Data data, final Input input, final List<DataValidationError> validationErrors )
    {
        final int maxOccurrences = input.getOccurrences().getMaximum();
        if ( maxOccurrences > 0 && data.hasArrayAsValue() )
        {
            final int size = data.getDataArray().size();
            if ( size > maxOccurrences )
            {
                validationErrors.add( new MaximumOccurrencesValidationError( input, size ) );
            }
        }
    }

    private void validateFormItems( final Iterable<FormItem> formItems, final EntrySelector entrySelector,
                                    final List<DataValidationError> validationErrors )
    {
        // check missing required entries
        for ( FormItem formItem : formItems )
        {
            if ( formItem instanceof Input )
            {
                validateInput( (Input) formItem, entrySelector, validationErrors );
            }
            else if ( formItem instanceof FormItemSet )
            {
                validateSet( (FormItemSet) formItem, entrySelector, validationErrors );
            }
            else if ( formItem instanceof FieldSet )
            {
                validateFormItems( ( (FieldSet) formItem ).formItemIterable(), entrySelector, validationErrors );
            }
        }
    }

    private void validateInput( final Input input, final EntrySelector entrySelector, final List<DataValidationError> validationErrors )
    {
        final Data data = getData( input, entrySelector );
        if ( input.isRequired() )
        {
            verifyRequiredInput( input, data, validationErrors );
        }
    }

    private void validateSet( final FormItemSet formItemSet, final EntrySelector entrySelector,
                              final List<DataValidationError> validationErrors )
    {
        final DataSet dataSet = getDataSet( formItemSet, entrySelector, validationErrors );
        if ( formItemSet.isRequired() )
        {
            verifyRequiredFormItemSet( formItemSet, dataSet, validationErrors );
        }

        if ( dataSet != null )
        {
            validateFormItems( formItemSet.getFormItems().iterable(), dataSet, validationErrors );
        }
        else
        {
            validateFormItems( formItemSet.getFormItems().iterable(), null, validationErrors );
        }
    }

    private void verifyRequiredInput( final Input input, final Data data, final List<DataValidationError> validationErrors )
    {
        if ( data == null )
        {
            validationErrors.add( new MinimumOccurrencesValidationError( input, 0 ) );
        }
        else
        {
            checkBreaksMinimumOccurrencesContract( input, data, validationErrors );
        }
    }

    private void checkBreaksMinimumOccurrencesContract( final Input input, final Data data,
                                                        final List<DataValidationError> validationErrors )
    {
        if ( input.isRequired() )
        {
            if ( data.hasArrayAsValue() )
            {
                final DataArray dataArray = data.getDataArray();
                if ( dataArray.size() < input.getOccurrences().getMinimum() )
                {
                    validationErrors.add( new MinimumOccurrencesValidationError( input, dataArray.size() ) );
                }
                int max = Math.min( dataArray.size(), input.getOccurrences().getMinimum() );
                for ( int i = 0; i < max; i++ )
                {
                    try
                    {
                        input.getInputType().checkBreaksRequiredContract( dataArray.getData( i ) );
                    }
                    catch ( BreaksRequiredContractException e )
                    {
                        validationErrors.add( new MissingRequiredValueValidationError( input, e.getValue() ) );
                    }
                }
            }
            else
            {
                try
                {
                    input.getInputType().checkBreaksRequiredContract( data );
                }
                catch ( BreaksRequiredContractException e )
                {
                    validationErrors.add( new MissingRequiredValueValidationError( input, e.getValue() ) );
                }
                if ( input.getOccurrences().getMinimum() > 1 )
                {
                    validationErrors.add( new MinimumOccurrencesValidationError( input, 1 ) );
                }
            }
        }
    }

    private void verifyRequiredFormItemSet( final FormItemSet formItemSet, final DataSet dataSet,
                                            final List<DataValidationError> validationErrors )
    {
        if ( dataSet == null )
        {
            validationErrors.add( new MissingRequiredValueValidationError( formItemSet ) );
        }
    }

    private Data getData( final Input input, final EntrySelector entrySelector )
    {
        return entrySelector != null ? entrySelector.getData( new EntryPath( input.getPath().toString() ) ) : null;
    }

    private DataSet getDataSet( final FormItemSet formItemSet, final EntrySelector entrySelector,
                                final List<DataValidationError> validationErrors )
    {
        try
        {
            return entrySelector != null ? entrySelector.getDataSet( new EntryPath( formItemSet.getPath().toString() ) ) : null;
        }
        catch ( IllegalArgumentException e )
        {
            validationErrors.add( new DataValidationError( formItemSet.getPath(), e.getMessage() ) );
            return null;
        }
    }

}
