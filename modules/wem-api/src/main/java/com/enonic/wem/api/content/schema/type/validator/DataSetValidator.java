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
public class DataSetValidator
{
    private ContentType contentType;

    private boolean recordExceptions = false;

    private boolean checkValidationRegexp;

    private final List<InvalidDataException> invalidDataExceptions = new ArrayList<>();

    private DataSetValidator()
    {
        // Protection
    }

    public void validate( final DataSet dataSet )
        throws InvalidDataException
    {
        doValidateEntries( dataSet );
    }

    public List<InvalidDataException> getInvalidDataExceptions()
    {
        return invalidDataExceptions;
    }

    private void doValidateEntries( final Iterable<Entry> entries )
    {
        for ( Entry data : entries )
        {
            doValidateData( data );
        }
    }

    private void doValidateData( final Entry entry )
        throws InvalidDataException
    {
        if ( entry.isDataSet() )
        {
            doValidateDataSet( entry.toDataSet() );
        }
        else
        {
            checkDataTypeValidity( entry.toData() );

            final FormItem formItem = contentType.form().getFormItem( entry.getPath().resolveFormItemPath().toString() );
            if ( formItem != null )
            {
                if ( formItem instanceof Input )
                {
                    checkInputValidity( entry.toData(), (Input) formItem );
                }
            }
        }
    }

    private void doValidateDataSet( final DataSet dataSet )
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
                        checkInputValidity( entry.toData(), (Input) subFormItem );
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
            doValidateEntries( dataSet );
        }
    }

    private void checkDataTypeValidity( final Data data )
    {
        try
        {
            data.checkDataTypeValidity();
        }
        catch ( InvalidDataException e )
        {
            registerInvalidDataException( e );
        }
    }

    private void checkInputValidity( final Data data, final Input input )
    {
        try
        {
            input.checkValidity( data );
        }
        catch ( InvalidDataException e )
        {
            registerInvalidDataException( e );
        }

        if ( checkValidationRegexp )
        {
            try
            {
                input.checkValidationRegexp( data );
            }
            catch ( InvalidDataException e )
            {
                registerInvalidDataException( e );
            }
        }
    }

    private void registerInvalidDataException( final InvalidDataException invalidDataException )
    {
        if ( !recordExceptions )
        {
            throw invalidDataException;
        }

        invalidDataExceptions.add( invalidDataException );
    }

    public static Builder newValidator()
    {
        return new Builder();
    }

    public static class Builder
    {
        private DataSetValidator validator = new DataSetValidator();

        public Builder contentType( ContentType contentType )
        {
            validator.contentType = contentType;
            return this;
        }

        public Builder recordExceptions( boolean value )
        {
            validator.recordExceptions = value;
            return this;
        }

        public Builder checkValidationRegexp( boolean value )
        {
            validator.checkValidationRegexp = value;
            return this;
        }

        public DataSetValidator build()
        {
            Preconditions.checkNotNull( validator.contentType, "contentType is required" );
            return validator;
        }

    }

}
