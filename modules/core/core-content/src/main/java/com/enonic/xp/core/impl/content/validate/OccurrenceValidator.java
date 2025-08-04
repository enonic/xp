package com.enonic.xp.core.impl.content.validate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
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

@Component
public final class OccurrenceValidator
    implements ContentValidator
{
    private static final String OPTION_SET_SELECTION_ARRAY_NAME = "_selected";

    @Override
    public void validate( final ContentValidatorParams params, final ValidationErrors.Builder validationErrorsBuilder )
    {
        validate( params.getContentType().getForm(), params.getData().getRoot(), validationErrorsBuilder );
    }

    public static void validate( final Form form, final PropertySet dataSet, final ValidationErrors.Builder validationErrorsBuilder )
    {
        validate( form, List.of( dataSet ), validationErrorsBuilder );
    }

    private static void validate( final Iterable<FormItem> formItems, final List<PropertySet> parentDataSets,
                                  final ValidationErrors.Builder validationErrorsBuilder )
    {
        for ( FormItem formItem : formItems )
        {
            validateOccurrences( formItem, parentDataSets, validationErrorsBuilder );

            if ( formItem instanceof FormItemSet formItemSet )
            {
                final List<PropertySet> dataSets = getDataSets( formItem.getName(), parentDataSets );
                validate( formItemSet, dataSets, validationErrorsBuilder );
            }
            else if ( formItem instanceof FieldSet fieldSet )
            {
                validate( fieldSet, parentDataSets, validationErrorsBuilder );
            }
            else if ( formItem instanceof FormOptionSet )
            {
                validateFormOptionSetSelection( (FormOptionSet) formItem, parentDataSets, validationErrorsBuilder );
            }
        }
    }

    private static void validateFormOptionSetSelection( final FormOptionSet formOptionSet, final List<PropertySet> parentDataSets,
                                                        final ValidationErrors.Builder validationErrorsBuilder )
    {
        final List<PropertySet> propertySets = getDataSets( formOptionSet.getName(), parentDataSets );

        for ( PropertySet propertySet : propertySets )
        {
            boolean hasSelectionArray =
                propertySet.getPropertyArrays().stream().anyMatch( array -> array.getValueType().equals( ValueTypes.STRING ) );

            final List<Property> selectedItems = propertySet.getProperties( OPTION_SET_SELECTION_ARRAY_NAME );

            int numberOfOptions = hasSelectionArray
                ? selectedItems.size()
                : Math.toIntExact(
                    StreamSupport.stream( formOptionSet.spliterator(), false ).filter( FormOptionSetOption::isDefaultOption ).count() );

            final Occurrences occurrences = formOptionSet.getMultiselection();

            if ( numberOfOptions < occurrences.getMinimum() ||
                ( occurrences.getMaximum() != 0 && numberOfOptions > occurrences.getMaximum() ) )
            {
                validationErrorsBuilder.add(
                    ValidationError.dataError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "cms.validation.occurrencesInvalid" ),
                                               propertySet.getProperty().getPath() )
                        .i18n( "system.cms.validation.optionsetOccurrencesInvalid" )
                        .args( formOptionSet.getPath(), occurrences.getMinimum(), occurrences.getMaximum(), numberOfOptions )
                        .build() );
            }

            for ( final FormOptionSetOption option : formOptionSet )
            {
                if ( hasSelectionArray && optionIsSelected( option, selectedItems ) )
                {
                    validate( option.getFormItems(),
                              Optional.ofNullable( propertySet.getSet( option.getName() ) ).map( List::of ).orElse( List.of() ),
                              validationErrorsBuilder );
                }
            }
        }
    }

    private static boolean optionIsSelected( final FormOptionSetOption option, final List<Property> selectedItems )
    {
        return selectedItems.stream().anyMatch( elem -> elem.getString().equals( option.getName() ) );
    }

    private static void validateOccurrences( final FormItem formItem, final List<PropertySet> parentDataSets,
                                             final ValidationErrors.Builder validationErrorsBuilder )
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

            final PropertyPath parentPath = Optional.ofNullable( parentDataSet.getProperty() )
                .map( Property::getPath )
                .orElseGet( () -> PropertyPath.from( formItem.getPath().getParent().toString() ) );

            final PropertyPath path = PropertyPath.from( parentPath, formItem.getName() );

            final int minOccurrences = occurrences.getMinimum();

            if ( occurrences.impliesRequired() && entryCount < minOccurrences )
            {
                validationErrorsBuilder.add(
                    ValidationError.dataError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "cms.occurrencesInvalid" ), path )
                        .i18n( "system.cms.validation.minOccurrencesInvalid" )
                        .args( formItem.getPath(), minOccurrences, entryCount )
                        .build() );
            }

            final int maxOccurrences = occurrences.getMaximum();

            if ( maxOccurrences > 0 && entryCount > maxOccurrences )
            {
                validationErrorsBuilder.add(
                    ValidationError.dataError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "cms.occurrencesInvalid" ), path )
                        .i18n( "system.cms.validation.maxOccurrencesInvalid" )
                        .args( formItem.getPath(), occurrences.getMaximum(), entryCount )
                        .build() );
            }
        }
    }

    private static List<PropertySet> getDataSets( final String name, final List<PropertySet> parentDataSets )
    {
        return parentDataSets.stream()
            .flatMap( parentDataSet -> StreamSupport.stream( parentDataSet.getSets( name ).spliterator(), false ) )
            .collect( Collectors.toList() );
    }
}
