package com.enonic.wem.api.content.schema.content.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.content.schema.content.form.FieldSet;
import com.enonic.wem.api.content.schema.content.form.Form;
import com.enonic.wem.api.content.schema.content.form.FormItem;
import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.Input;


final class MinimumOccurrencesValidator
{
    private final List<DataValidationError> validationErrors = Lists.newArrayList();

    final List<DataValidationError> validationErrors()
    {
        return Collections.unmodifiableList( validationErrors );
    }

    final void validate( final Form form, final RootDataSet dataSet )
    {
        final List<DataSet> parentDataSets = Lists.newArrayList();
        parentDataSets.add( dataSet );
        validate( form, parentDataSets );
    }

    private void validate( final Iterable<FormItem> formItems, final List<DataSet> parentDataSets )
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

    private void validateInput( final Input input, final List<DataSet> parentDataSets )
    {
        if ( input.isRequired() )
        {
            for ( DataSet parentDataSet : parentDataSets )
            {
                final int entryCount = parentDataSet.entryCount( input.getName() );
                final int occurrencesToCheck = Math.min( entryCount, input.getOccurrences().getMinimum() );
                for ( int i = 0; i < occurrencesToCheck; i++ )
                {
                    final Property property = parentDataSet.getProperty( input.getName(), i );
                    try
                    {
                        input.getInputType().checkBreaksRequiredContract( property );
                    }
                    catch ( BreaksRequiredContractException e )
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

    private void validateFormItemSet( final FormItemSet formItemSet, final List<DataSet> parentDataSets )
    {
        if ( formItemSet.isRequired() )
        {
            for ( final DataSet parentDataSet : parentDataSets )
            {
                final int entryCount = parentDataSet.entryCount( formItemSet.getName() );
                if ( entryCount < formItemSet.getOccurrences().getMinimum() )
                {
                    // TODO: include information about missing entry path?
                    validationErrors.add( new MinimumOccurrencesValidationError( formItemSet, entryCount ) );
                }
            }
        }

        final List<DataSet> dataSets = getDataSets( formItemSet.getName(), parentDataSets );
        validate( formItemSet.getFormItems(), dataSets );
    }

    private List<DataSet> getDataSets( final String name, final List<DataSet> parentDataSets )
    {
        final List<DataSet> dataSets = new ArrayList<>();
        for ( DataSet parentDataSet : parentDataSets )
        {
            dataSets.addAll( parentDataSet.dataSets( name ) );
        }
        return dataSets;
    }
}

