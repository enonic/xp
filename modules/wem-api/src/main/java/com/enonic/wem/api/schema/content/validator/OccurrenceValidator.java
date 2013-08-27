package com.enonic.wem.api.schema.content.validator;


import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.wem.api.data.ContentData;
import com.enonic.wem.api.schema.content.ContentType;

public final class OccurrenceValidator
{
    private final ContentType contentType;

    public OccurrenceValidator( final ContentType contentType )
    {
        Preconditions.checkNotNull( contentType, "No contentType given" );
        this.contentType = contentType;
    }

    public DataValidationErrors validate( final ContentData contentData )
    {
        final List<DataValidationError> validationErrors = Lists.newArrayList();

        final MinimumOccurrencesValidator minimum = new MinimumOccurrencesValidator();
        minimum.validate( contentType.form(), contentData );
        validationErrors.addAll( minimum.validationErrors() );

        final MaximumOccurrencesValidator maximum = new MaximumOccurrencesValidator( contentType );
        maximum.validate( contentData );
        validationErrors.addAll( maximum.validationErrors() );

        return DataValidationErrors.from( validationErrors );
    }
}
