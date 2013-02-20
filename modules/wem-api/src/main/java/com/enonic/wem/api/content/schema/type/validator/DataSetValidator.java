package com.enonic.wem.api.content.schema.type.validator;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.content.schema.type.form.FormItem;
import com.enonic.wem.api.content.schema.type.form.FormItemSet;
import com.enonic.wem.api.content.schema.type.form.Input;
import com.enonic.wem.api.content.schema.type.form.InvalidDataException;

/**
 * Validates that given DataSet is valid, meaning it is of valid:
 * type, format, value.
 */
public final class DataSetValidator
{
    private final ContentType contentType;

    public DataSetValidator( final ContentType contentType )
    {
        Preconditions.checkNotNull( contentType, "contentType is required" );
        this.contentType = contentType;
    }

    public DataValidationErrors validate( final DataSet dataSet )
        throws InvalidDataException
    {
        final List<DataValidationError> validationErrors = new ArrayList<>();

        validateEntries( dataSet, validationErrors );

        return DataValidationErrors.from( validationErrors );
    }

    private void validateEntries( final Iterable<Entry> entries, final List<DataValidationError> validationErrors )
    {
        for ( Entry data : entries )
        {
            validateData( data, validationErrors );
        }
    }

    private void validateData( final Entry entry, final List<DataValidationError> validationErrors )
        throws InvalidDataException
    {
        if ( entry.isDataSet() )
        {
            validateDataSet( entry.toDataSet(), validationErrors );
        }
        else
        {
            checkDataTypeValidity( entry.toData(), validationErrors );

            final FormItem formItem = contentType.form().getFormItem( entry.getPath().resolveFormItemPath().toString() );
            if ( formItem != null )
            {
                if ( formItem instanceof Input )
                {
                    checkInputValidity( entry.toData(), (Input) formItem, validationErrors );
                }
            }
        }
    }

    private void validateDataSet( final DataSet dataSet, final List<DataValidationError> validationErrors )
    {
        final String path = dataSet.getPath().resolveFormItemPath().toString();
        final FormItem formItem = contentType.form().getFormItem( path );
        if ( formItem != null )
        {
            if ( formItem instanceof FormItemSet )
            {
                for ( Entry entry : dataSet )
                {
                    final FormItem subFormItem = contentType.form().getFormItem( entry.getPath().resolveFormItemPath().toString() );
                    if ( subFormItem instanceof Input )
                    {
                        checkInputValidity( entry.toData(), (Input) subFormItem, validationErrors );
                    }
                }
            }
            else
            {
                throw new IllegalArgumentException(
                    "FormItem at path [" + path + "] expected to be a FormItemSet: " + formItem.getClass().getSimpleName() );
            }
        }
        else
        {
            validateEntries( dataSet, validationErrors );
        }
    }

    private void checkDataTypeValidity( final Data data, final List<DataValidationError> validationErrors )
    {
        try
        {
            data.checkDataTypeValidity();
        }
        catch ( InvalidDataException e )
        {
            validationErrors.add( translateInvalidDataException( e ) );
        }
    }

    private void checkInputValidity( final Data data, final Input input, final List<DataValidationError> validationErrors )
    {
        try
        {
            input.checkValidity( data );
        }
        catch ( InvalidDataException e )
        {
            validationErrors.add( translateInvalidDataException( e ) );
        }

        try
        {
            if ( input.getValidationRegexp() != null )
            {
                input.checkValidationRegexp( data );
            }
        }
        catch ( InvalidDataException e )
        {
            validationErrors.add( translateInvalidDataException( e ) );
        }
    }

    private DataValidationError translateInvalidDataException( final InvalidDataException invalidDataException )
    {
        return new DataValidationError( invalidDataException.getData().getPath().resolveFormItemPath(), invalidDataException.getMessage() );
    }

}
