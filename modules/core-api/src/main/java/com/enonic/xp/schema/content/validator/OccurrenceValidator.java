package com.enonic.xp.schema.content.validator;


import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.form.Form;

@Beta
public final class OccurrenceValidator
{
    private final Form form;

    public OccurrenceValidator( final Form form )
    {
        Preconditions.checkNotNull( form, "No form given" );
        this.form = form;
    }

    public DataValidationErrors validate( final PropertySet propertySet )
    {
        final List<DataValidationError> validationErrors = Lists.newArrayList();

        final MinimumOccurrencesValidator minimum = new MinimumOccurrencesValidator();

        minimum.validate( form, propertySet );
        validationErrors.addAll( minimum.validationErrors() );

        final MaximumOccurrencesValidator maximum = new MaximumOccurrencesValidator( this.form );

        maximum.validate( propertySet );
        validationErrors.addAll( maximum.validationErrors() );

        return DataValidationErrors.from( validationErrors );
    }
}
