package com.enonic.xp.core.impl.content.validate;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
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
        this.validateInputOccurrences( input, parentDataSets );
    }

    private void validateFormOptionSet( final FormOptionSet formOptionSet, final List<PropertySet> parentDataSets )
    {
        this.validateOptionSetOccurrences( formOptionSet, parentDataSets );

        this.validateFormOptionSetSelection( formOptionSet, parentDataSets );
    }

    private void validateFormOptionSetSelection( final FormOptionSet formOptionSet, final List<PropertySet> parentDataSets )
    {
        final List<PropertySet> optionSetOccurrencePropertySets = getDataSets( formOptionSet.getName(), parentDataSets );

        for ( PropertySet optionSetOccurrencePropertySet : optionSetOccurrencePropertySets )
        {

            validateOptionSetSelection( formOptionSet, optionSetOccurrencePropertySet.getPropertyNames().length );

            for ( final FormOptionSetOption option : formOptionSet )
            {
                if ( optionIsSelected( option, optionSetOccurrencePropertySet ) )
                {
                    final List<PropertySet> optionDataSets = Lists.newArrayList();
                    optionDataSets.add( optionSetOccurrencePropertySet.getSet( option.getName() ) );
                    validate( option.getFormItems(), optionDataSets );
                }
            }
        }
    }

    private boolean optionIsSelected( final FormOptionSetOption option, final PropertySet parentPropertySet )
    {
        return parentPropertySet.getSet( option.getName() ) != null;
    }

    private void validateOptionSetSelection( final FormOptionSet formOptionSet, int numberOfSelectedOptions )
    {
        if ( numberOfSelectedOptions > formOptionSet.getMultiselection().getMaximum() ||
            numberOfSelectedOptions < formOptionSet.getMultiselection().getMinimum() )
        {
            validationErrors.add( new OptionSetSelectionValidationError( formOptionSet, numberOfSelectedOptions ) );
        }
    }

    private void validateFormItemSet( final FormItemSet formItemSet, final List<PropertySet> parentDataSets )
    {

        this.validateItemSetOccurrences( formItemSet, parentDataSets );

        final List<PropertySet> dataSets = getDataSets( formItemSet.getName(), parentDataSets );
        validate( formItemSet.getFormItems(), dataSets );
    }

    private void validateInputOccurrences( final Input input, final List<PropertySet> parentDataSets )
    {

        for ( final PropertySet parentDataSet : parentDataSets )
        {
            final int entryCount = parentDataSet.countProperties( input.getName() );
            if ( input.isRequired() && entryCount < input.getOccurrences().getMinimum() )
            {
                validationErrors.add(
                    new MinimumOccurrencesValidationError( input.getPath(), "Input", input.getOccurrences(), entryCount ) );
            }

            final int maxOccurrences = input.getOccurrences().getMaximum();
            if ( maxOccurrences > 0 && entryCount > maxOccurrences )
            {
                validationErrors.add(
                    new MaximumOccurrencesValidationError( input.getPath(), "Input", input.getOccurrences(), entryCount ) );
            }
        }
    }

    private void validateItemSetOccurrences( final FormItemSet formItemSet, final List<PropertySet> parentDataSets )
    {

        for ( final PropertySet parentDataSet : parentDataSets )
        {
            final int entryCount = parentDataSet.countProperties( formItemSet.getName() );
            if ( formItemSet.isRequired() && entryCount < formItemSet.getOccurrences().getMinimum() )
            {
                validationErrors.add(
                    new MinimumOccurrencesValidationError( formItemSet.getPath(), "FormItemSet", formItemSet.getOccurrences(),
                                                           entryCount ) );
            }

            final int maxOccurrences = formItemSet.getOccurrences().getMaximum();
            if ( maxOccurrences > 0 && entryCount > maxOccurrences )
            {
                validationErrors.add(
                    new MaximumOccurrencesValidationError( formItemSet.getPath(), "FormItemSet", formItemSet.getOccurrences(),
                                                           entryCount ) );
            }
        }
    }

    private void validateOptionSetOccurrences( final FormOptionSet formOptionSet, final List<PropertySet> parentDataSets )
    {

        for ( final PropertySet parentDataSet : parentDataSets )
        {
            final int entryCount = parentDataSet.countProperties( formOptionSet.getName() );
            if ( formOptionSet.isRequired() && entryCount < formOptionSet.getOccurrences().getMinimum() )
            {
                validationErrors.add(
                    new MinimumOccurrencesValidationError( formOptionSet.getPath(), "FormOptionSet", formOptionSet.getOccurrences(),
                                                           entryCount ) );
            }

            final int maxOccurrences = formOptionSet.getOccurrences().getMaximum();
            if ( maxOccurrences > 0 && entryCount > maxOccurrences )
            {
                validationErrors.add(
                    new MaximumOccurrencesValidationError( formOptionSet.getPath(), "FormOptionSet", formOptionSet.getOccurrences(),
                                                           entryCount ) );
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
