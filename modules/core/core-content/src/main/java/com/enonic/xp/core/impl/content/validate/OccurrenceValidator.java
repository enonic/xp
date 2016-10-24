package com.enonic.xp.core.impl.content.validate;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.xp.data.PropertyArray;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.GenericFormItem;
import com.enonic.xp.form.Input;

public final class OccurrenceValidator
{
    private final Form form;

    private final List<DataValidationError> validationErrors = Lists.newArrayList();

    public OccurrenceValidator( final Form form )
    {
        Preconditions.checkNotNull( form, "No form given" );
        this.form = form;
    }

    public DataValidationErrors validate( final PropertySet propertySet )
    {

        this.validate( this.form, propertySet );

        return DataValidationErrors.from( validationErrors );
    }

    private void validate( final Form form, final PropertySet dataSet )
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
            else if ( formItem instanceof FormOptionSet )
            {
                validateFormOptionSet( (FormOptionSet) formItem, parentDataSets );
            }
        }
    }

    private void validateInput( final Input input, final List<PropertySet> parentDataSets )
    {
        this.validateFormItemOccurrences( input, parentDataSets );
    }

    private void validateFormOptionSet( final FormOptionSet formOptionSet, final List<PropertySet> parentDataSets )
    {
        this.validateFormItemOccurrences( formOptionSet, parentDataSets );

        final List<PropertySet> optionSetOccurrencePropertySets = getDataSets( formOptionSet.getName(), parentDataSets );
        for ( PropertySet optionSetOccurrencePropertySet : optionSetOccurrencePropertySets )
        {
            final PropertyArray selectionArray = optionSetOccurrencePropertySet.getPropertyArray( formOptionSet.getName() + "_selection" );
            if ( selectionArray != null )
            {
                int numberOfSelectedOptions = selectionArray.size();
                if ( numberOfSelectedOptions > formOptionSet.getMultiselection().getMaximum() ||
                    numberOfSelectedOptions < formOptionSet.getMultiselection().getMinimum() )
                {
                    validationErrors.add( new OptionSetSelectionValidationError( formOptionSet, numberOfSelectedOptions ) );
                }
            }
            else
            {
                int numberOfDefaultOptions = formOptionSet.getNumberOfDefaultOptions();
                if ( numberOfDefaultOptions > formOptionSet.getMultiselection().getMaximum() ||
                    numberOfDefaultOptions < formOptionSet.getMultiselection().getMinimum() )
                {
                    validationErrors.add( new OptionSetSelectionValidationError( formOptionSet, numberOfDefaultOptions ) );
                }
            }

            for ( final FormOptionSetOption option : formOptionSet.getOptions() )
            {
                if ( ( option.isDefaultOption() && selectionArray == null ) ||
                    selectionArray.getValues().stream().anyMatch( elem -> elem.toString().equals( option.getName() ) ) )
                {
                    final List<PropertySet> optionDataSets = Lists.newArrayList();
                    optionDataSets.add( optionSetOccurrencePropertySet.getSet( option.getName() ) );
                    validate( option.getFormItems(), optionDataSets );
                }
            }
        }
    }

    private void validateFormItemSet( final FormItemSet formItemSet, final List<PropertySet> parentDataSets )
    {

        this.validateFormItemOccurrences( formItemSet, parentDataSets );

        final List<PropertySet> dataSets = getDataSets( formItemSet.getName(), parentDataSets );
        validate( formItemSet.getFormItems(), dataSets );
    }

    private void validateFormItemOccurrences( final GenericFormItem genericFormItem, final List<PropertySet> parentDataSets )
    {

        for ( final PropertySet parentDataSet : parentDataSets )
        {
            final int entryCount = parentDataSet.countProperties( genericFormItem.getName() );
            if ( genericFormItem.isRequired() && entryCount < genericFormItem.getOccurrences().getMinimum() )
            {
                // TODO: include information about missing DataPath?
                validationErrors.add( new MinimumOccurrencesValidationError( genericFormItem, entryCount ) );
            }

            final int maxOccurrences = genericFormItem.getOccurrences().getMaximum();
            if ( maxOccurrences > 0 && entryCount > maxOccurrences )
            {
                validationErrors.add( new MaximumOccurrencesValidationError( genericFormItem, entryCount ) );
            }
        }
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
