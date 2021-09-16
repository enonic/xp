package com.enonic.xp.core.impl.content.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.DataValidationError;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;

public final class OccurrenceValidator
{
    private static final String OPTION_SET_SELECTION_ARRAY_NAME = "_selected";

    private final Form form;

    private final List<ValidationError> validationErrors = new ArrayList<>();

    public OccurrenceValidator( final Form form )
    {
        Preconditions.checkNotNull( form, "No form given" );
        this.form = form;
    }

    public ValidationErrors validate( final PropertySet propertySet )
    {
        this.validate( this.form, propertySet );

        return ValidationErrors.from( validationErrors );
    }

    private void validate( final Form form, final PropertySet dataSet )
    {
        validate( form, List.of( dataSet ) );
    }

    private void validate( final Iterable<FormItem> formItems, final List<PropertySet> parentDataSets )
    {
        for ( FormItem formItem : formItems )
        {
            this.validateOccurrences( formItem, parentDataSets );

            if ( formItem instanceof FormItemSet )
            {
                final List<PropertySet> dataSets = getDataSets( formItem.getName(), parentDataSets );
                validate( ( (FormItemSet) formItem ).getFormItems(), dataSets );
            }
            else if ( formItem instanceof FieldSet )
            {
                validate( ( (FieldSet) formItem ).formItemIterable(), parentDataSets );
            }
            else if ( formItem instanceof FormOptionSet )
            {
                this.validateFormOptionSetSelection( (FormOptionSet) formItem, parentDataSets );
            }
        }
    }

    private void validateFormOptionSetSelection( final FormOptionSet formOptionSet, final List<PropertySet> parentDataSets )
    {
        final List<PropertySet> optionSetOccurrencePropertySets = getDataSets( formOptionSet.getName(), parentDataSets );

        for ( PropertySet optionSetOccurrencePropertySet : optionSetOccurrencePropertySets )
        {
            boolean hasSelectionArray = optionSetOccurrencePropertySet.getPropertyArrays()
                .stream()
                .anyMatch( array -> array.getValueType().equals( ValueTypes.STRING ) );

            final List<Property> selectedItems = optionSetOccurrencePropertySet.getProperties( OPTION_SET_SELECTION_ARRAY_NAME );

            if ( hasSelectionArray )
            {
                validateOptionSetSelection( formOptionSet, selectedItems.size(), optionSetOccurrencePropertySet.getProperty().getPath() );
            }
            else
            {
                validateDefaultOptionSetSelection( formOptionSet, optionSetOccurrencePropertySet.getProperty().getPath() );
            }

            for ( final FormOptionSetOption option : formOptionSet )
            {
                if ( hasSelectionArray && optionIsSelected( option, selectedItems ) )
                {
                    validate( option.getFormItems(), Optional.ofNullable( optionSetOccurrencePropertySet.getSet( option.getName() ) )
                        .map( List::of )
                        .orElse( List.of() ) );
                }
            }
        }
    }

    private boolean optionIsSelected( final FormOptionSetOption option, final List<Property> selectedItems )
    {
        return selectedItems.stream().anyMatch( elem -> elem.getString().equals( option.getName() ) );
    }

    private void validateDefaultOptionSetSelection( final FormOptionSet formOptionSet, final PropertyPath path )
    {
        final int numberOfOptions = Math.toIntExact(
            StreamSupport.stream( formOptionSet.spliterator(), false ).filter( FormOptionSetOption::isDefaultOption ).count() );

        validateOptionSetSelection( formOptionSet, numberOfOptions, path );
    }

    private void validateOptionSetSelection( final FormOptionSet formOptionSet, int numberOfOptions, final PropertyPath path )
    {
        if ( numberOfOptions < formOptionSet.getMultiselection().getMinimum() ||
            ( formOptionSet.getMultiselection().getMaximum() != 0 && numberOfOptions > formOptionSet.getMultiselection().getMaximum() ) )
        {
            validationErrors.add(
                new DataValidationError( path, "OptionSet [{0}] requires min {1} max {2} items selected: {3}", formOptionSet.getPath(),
                                         formOptionSet.getMultiselection().getMinimum(), formOptionSet.getMultiselection().getMaximum(),
                                         numberOfOptions ) );
        }
    }

    private void validateOccurrences( final FormItem formItem, final List<PropertySet> parentDataSets )
    {
        final Occurrences occurrences;
        if ( formItem instanceof Input )
        {
            occurrences = ( (Input) formItem ).getOccurrences();
        }
        else if ( formItem instanceof FormItemSet )
        {
            occurrences = ( (FormItemSet) formItem ).getOccurrences();
        }
        else if ( formItem instanceof FormOptionSet )
        {
            occurrences = ( (FormOptionSet) formItem ).getOccurrences();
        }
        else
        {
            return;
        }

        for ( final PropertySet parentDataSet : parentDataSets )
        {
            final int entryCount = parentDataSet.countProperties( formItem.getName() );
            final Property property = parentDataSet.getProperty();
            final PropertyPath propertyPath;
            if ( property == null )
            {
                propertyPath = PropertyPath.from( formItem.getPath().toString() );
            }
            else
            {
                propertyPath = property.getPath();
            }

            if ( occurrences.impliesRequired() && entryCount < occurrences.getMinimum() )
            {
                validationErrors.add( new DataValidationError( propertyPath, formItem.getClass().getSimpleName() +
                    " [{0}] requires minimum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}", formItem.getPath(),
                                                               occurrences.getMinimum(), entryCount ) );
            }

            final int maxOccurrences = occurrences.getMaximum();
            if ( maxOccurrences > 0 && entryCount > maxOccurrences )
            {
                validationErrors.add( new DataValidationError( propertyPath, formItem.getClass().getSimpleName() +
                    " [{0}] allows maximum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}", formItem.getPath(), occurrences.getMaximum(),
                                                               entryCount ) );
            }
        }
    }

    private List<PropertySet> getDataSets( final String name, final List<PropertySet> parentDataSets )
    {
        return parentDataSets.stream()
            .flatMap( parentDataSet -> StreamSupport.stream( parentDataSet.getSets( name ).spliterator(), false ) )
            .collect( Collectors.toList() );
    }
}
