package com.enonic.wem.api.content.schema.content.validator;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.form.FormItem;
import com.enonic.wem.api.content.schema.content.form.FormItemPath;
import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.Input;


class MaximumOccurrencesValidator
{
    private final List<DataValidationError> validationErrors = Lists.newArrayList();

    private final ContentType contentType;

    MaximumOccurrencesValidator( final ContentType contentType )
    {
        Preconditions.checkNotNull( contentType, "No contentType given" );
        this.contentType = contentType;
    }

    final List<DataValidationError> validationErrors()
    {
        return Collections.unmodifiableList( validationErrors );
    }

    void validate( final RootDataSet dataSet )
    {
        for ( final String entryName : dataSet.entryNames() )
        {
            final FormItemPath parentFormItemPath = dataSet.getPath().resolveFormItemPath();
            final FormItemPath path = new FormItemPath( parentFormItemPath, entryName );
            final FormItem formItem = contentType.form().getFormItem( path );

            if ( formItem instanceof Input )
            {
                validateMaxOccurrences( dataSet, (Input) formItem );
            }
            else if ( formItem instanceof FormItemSet )
            {
                validateMaxOccurrences( dataSet, (FormItemSet) formItem );
            }
        }
    }

    private void validateMaxOccurrences( final DataSet parentDataSet, final FormItemSet formItemSet )
    {
        final int maxOccurrences = formItemSet.getOccurrences().getMaximum();
        if ( maxOccurrences > 0 )
        {
            final int size = parentDataSet.entryCount( formItemSet.getName() );
            if ( size > maxOccurrences )
            {
                validationErrors.add( new MaximumOccurrencesValidationError( formItemSet, size ) );
            }
        }
    }

    private void validateMaxOccurrences( final DataSet parentDataSet, final Input input )
    {
        final int maxOccurrences = input.getOccurrences().getMaximum();
        if ( maxOccurrences > 0 )
        {
            final int size = parentDataSet.entryCount( input.getName() );
            if ( size > maxOccurrences )
            {
                validationErrors.add( new MaximumOccurrencesValidationError( input, size ) );
            }
        }
    }
}
