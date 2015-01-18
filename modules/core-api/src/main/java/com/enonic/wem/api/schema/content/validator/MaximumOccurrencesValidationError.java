package com.enonic.wem.api.schema.content.validator;

import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;

public final class MaximumOccurrencesValidationError
    extends DataValidationError
{
    public MaximumOccurrencesValidationError( final FormItemSet set, final int size )
    {
        super( set.getPath(), "FormItemSet [{0}] allows maximum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}", set.getPath(),
               set.getOccurrences().getMaximum(), size );
    }

    public MaximumOccurrencesValidationError( final Input input, final int size )
    {
        super( input.getPath(), "Input [{0}] allows maximum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}", input.getPath(),
               input.getOccurrences().getMaximum(), size );
    }
}
