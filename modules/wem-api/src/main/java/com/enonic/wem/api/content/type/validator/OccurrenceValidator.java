package com.enonic.wem.api.content.type.validator;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.type.ContentType;

public final class OccurrenceValidator
{
    private final ContentType contentType;

    public OccurrenceValidator( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public DataValidationErrors validate( final DataSet dataSet )
    {
        final List<DataValidationError> validationErrors = Lists.newArrayList();

        final MinimumOccurrencesValidator minimum = new MinimumOccurrencesValidator();
        minimum.validate( contentType.form(), dataSet );
        validationErrors.addAll( minimum.validationErrors() );

        final MaximumOccurrencesValidator maximum = new MaximumOccurrencesValidator( contentType );
        maximum.validate( dataSet );
        validationErrors.addAll( maximum.validationErrors() );

        return DataValidationErrors.from( validationErrors );
    }
}
