package com.enonic.wem.api.content.schema.content.validator;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.form.FormItem;
import com.enonic.wem.api.content.schema.content.form.FormItemPath;
import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.InvalidDataException;

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
            checkDataTypeValidity( entry.toProperty(), validationErrors );

            final FormItem formItem = contentType.form().getFormItem( FormItemPath.from( entry.getPath().resolvePathElementNames() ) );
            if ( formItem != null )
            {
                if ( formItem instanceof Input )
                {
                    checkInputValidity( entry.toProperty(), (Input) formItem, validationErrors );
                }
            }
        }
    }

    private void validateDataSet( final DataSet dataSet, final List<DataValidationError> validationErrors )
    {
        final FormItemPath formItemPath = FormItemPath.from( dataSet.getPath().resolvePathElementNames() );
        final FormItem formItem = contentType.form().getFormItem( formItemPath );
        if ( formItem != null )
        {
            if ( formItem instanceof FormItemSet )
            {
                for ( final Entry entry : dataSet )
                {
                    final FormItem subFormItem =
                        contentType.form().getFormItem( FormItemPath.from( entry.getPath().resolvePathElementNames() ) );
                    if ( subFormItem instanceof Input )
                    {
                        checkInputValidity( entry.toProperty(), (Input) subFormItem, validationErrors );
                    }
                }
            }
            else
            {
                throw new IllegalArgumentException(
                    "FormItem at path [" + formItemPath + "] expected to be a FormItemSet: " + formItem.getClass().getSimpleName() );
            }
        }
        else
        {
            validateEntries( dataSet, validationErrors );
        }
    }

    private void checkDataTypeValidity( final Property property, final List<DataValidationError> validationErrors )
    {
        try
        {
            property.checkDataTypeValidity();
        }
        catch ( InvalidDataException e )
        {
            validationErrors.add( translateInvalidDataException( e ) );
        }
    }

    private void checkInputValidity( final Property property, final Input input, final List<DataValidationError> validationErrors )
    {
        try
        {
            input.checkValidity( property );
        }
        catch ( InvalidDataException e )
        {
            validationErrors.add( translateInvalidDataException( e ) );
        }

        try
        {
            if ( input.getValidationRegexp() != null )
            {
                input.checkValidationRegexp( property );
            }
        }
        catch ( InvalidDataException e )
        {
            validationErrors.add( translateInvalidDataException( e ) );
        }
    }

    private DataValidationError translateInvalidDataException( final InvalidDataException invalidDataException )
    {
        return new DataValidationError( FormItemPath.from( invalidDataException.getProperty().getPath().resolvePathElementNames() ),
                                        invalidDataException.getMessage() );
    }

}
