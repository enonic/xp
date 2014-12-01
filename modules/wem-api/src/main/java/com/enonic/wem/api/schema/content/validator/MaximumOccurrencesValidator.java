package com.enonic.wem.api.schema.content.validator;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemPath;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.schema.content.ContentType;


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

    void validate( final PropertyTree propertyTree )
    {
        final PropertySet root = propertyTree.getRoot();
        for ( final String entryName : root.getPropertyNames() )
        {
            final FormItemPath path = FormItemPath.from( FormItemPath.ROOT, entryName );
            final FormItem formItem = contentType.form().getFormItem( path );

            if ( formItem instanceof Input )
            {
                validateMaxOccurrences( root, (Input) formItem );
            }
            else if ( formItem instanceof FormItemSet )
            {
                validateMaxOccurrences( root, (FormItemSet) formItem );
            }
        }
    }

    private void validateMaxOccurrences( final PropertySet parentDataSet, final FormItemSet formItemSet )
    {
        final int maxOccurrences = formItemSet.getOccurrences().getMaximum();
        if ( maxOccurrences > 0 )
        {
            final int size = parentDataSet.countProperties( formItemSet.getName() );
            if ( size > maxOccurrences )
            {
                validationErrors.add( new MaximumOccurrencesValidationError( formItemSet, size ) );
            }
        }
    }

    private void validateMaxOccurrences( final PropertySet parentDataSet, final Input input )
    {
        final int maxOccurrences = input.getOccurrences().getMaximum();
        if ( maxOccurrences > 0 )
        {
            final int size = parentDataSet.countProperties( input.getName() );
            if ( size > maxOccurrences )
            {
                validationErrors.add( new MaximumOccurrencesValidationError( input, size ) );
            }
        }
    }
}
