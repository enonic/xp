package com.enonic.xp.core.impl.content.validate;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.form.Form;

public class ValueValidator
{
    private final Form form;

    public ValueValidator( final Form form )
    {
        Preconditions.checkNotNull( form, "No form given" );
        this.form = form;
    }

    public DataValidationErrors validate( final PropertySet propertySet )
    {
        final List<DataValidationError> validationErrors = Lists.newArrayList();

        final ValueOccurrenceValidator valueOccurrenceValidator = new ValueOccurrenceValidator();
        valueOccurrenceValidator.validate( form, propertySet );
        validationErrors.addAll( valueOccurrenceValidator.validationErrors() );

        return DataValidationErrors.from( validationErrors );
    }
}
