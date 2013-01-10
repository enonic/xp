package com.enonic.wem.api.content.type.validator;

import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.Input;

public final class MaximumOccurrencesValidationError
    extends DataValidationError
{
    public MaximumOccurrencesValidationError( final FormItemSet set, final int size )
    {
        super( set.getPath(), "FormItemSet [{0}] allows maximum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}", set,
               set.getOccurrences().getMaximum(), size );
    }

    public MaximumOccurrencesValidationError( final Input input, final int size )
    {
        super( input.getPath(), "Input [{0}] allows maximum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}", input,
               input.getOccurrences().getMaximum(), size );
    }
}
