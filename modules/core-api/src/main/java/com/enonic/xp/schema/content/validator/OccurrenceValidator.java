package com.enonic.xp.schema.content.validator;


import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.inputtype.InputTypeService;

@Beta
public final class OccurrenceValidator
{
    private final Form form;

    private final InputTypeService inputTypeService;

    public OccurrenceValidator( final Form form, final InputTypeService inputTypeService )
    {
        Preconditions.checkNotNull( form, "No form given" );
        this.form = form;
        this.inputTypeService = inputTypeService;
    }

    public DataValidationErrors validate( final PropertySet propertySet )
    {
        final List<DataValidationError> validationErrors = Lists.newArrayList();

        final MinimumOccurrencesValidator minimum = new MinimumOccurrencesValidator( this.inputTypeService );

        minimum.validate( form, propertySet );
        validationErrors.addAll( minimum.validationErrors() );

        final MaximumOccurrencesValidator maximum = new MaximumOccurrencesValidator( this.form );

        maximum.validate( propertySet );
        validationErrors.addAll( maximum.validationErrors() );

        return DataValidationErrors.from( validationErrors );
    }
}
