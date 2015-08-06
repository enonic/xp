package com.enonic.xp.schema.content.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.form.InputValidationException;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;


final class MinimumOccurrencesValidator
{
    private final List<DataValidationError> validationErrors = Lists.newArrayList();

    final List<DataValidationError> validationErrors()
    {
        return Collections.unmodifiableList( validationErrors );
    }

    final void validate( final Form form, final PropertySet dataSet )
    {
        final List<PropertySet> parentDataSets = Lists.newArrayList();
        parentDataSets.add( dataSet );
        validate( form, parentDataSets );
    }

    private void validate( final Iterable<FormItem> formItems, final List<PropertySet> parentDataSets )
    {
        for ( FormItem formItem : formItems )
        {
            if ( formItem instanceof Input )
            {
                validateInput( (Input) formItem, parentDataSets );
            }
            else if ( formItem instanceof FormItemSet )
            {
                validateFormItemSet( (FormItemSet) formItem, parentDataSets );
            }
            else if ( formItem instanceof FieldSet )
            {
                validate( ( (FieldSet) formItem ).formItemIterable(), parentDataSets );
            }
        }
    }

    private void validateInput( final Input input, final List<PropertySet> parentDataSets )
    {
        if ( input.isRequired() )
        {
            for ( PropertySet parentDataSet : parentDataSets )
            {
                final int entryCount = parentDataSet.countProperties( input.getName() );
                final int occurrencesToCheck = Math.min( entryCount, input.getOccurrences().getMinimum() );
                for ( int i = 0; i < occurrencesToCheck; i++ )
                {
                    final Property property = parentDataSet.getProperty( input.getName(), i );
                    try
                    {
                        input.getInputType().checkBreaksRequiredContract( property );
                    }
                    catch ( InputValidationException e )
                    {
                        validationErrors.add( new MissingRequiredValueValidationError( input, e.getProperty() ) );
                    }
                }

                if ( entryCount < input.getOccurrences().getMinimum() )
                {
                    validationErrors.add( new MinimumOccurrencesValidationError( input, entryCount ) );
                }
            }
        }
    }

    private void validateFormItemSet( final FormItemSet formItemSet, final List<PropertySet> parentDataSets )
    {
        if ( formItemSet.isRequired() )
        {
            for ( final PropertySet parentDataSet : parentDataSets )
            {
                final int entryCount = parentDataSet.countProperties( formItemSet.getName() );
                if ( entryCount < formItemSet.getOccurrences().getMinimum() )
                {
                    // TODO: include information about missing DataPath?
                    validationErrors.add( new MinimumOccurrencesValidationError( formItemSet, entryCount ) );
                }
            }
        }

        final List<PropertySet> dataSets = getDataSets( formItemSet.getName(), parentDataSets );
        validate( formItemSet.getFormItems(), dataSets );
    }

    private List<PropertySet> getDataSets( final String name, final List<PropertySet> parentDataSets )
    {
        final List<PropertySet> dataSets = new ArrayList<>();
        for ( final PropertySet parentDataSet : parentDataSets )
        {
            for ( final PropertySet set : parentDataSet.getSets( name ) )
            {
                dataSets.add( set );
            }
        }
        return dataSets;
    }
}

