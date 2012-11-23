package com.enonic.wem.api.content.type;

import com.enonic.wem.api.content.type.form.Input;

public final class MinimumOccurrencesValidationError
    extends DataValidationError
{

    public MinimumOccurrencesValidationError( final Input input, final int size )
    {
        super( input.getPath(), "Input [{0}] requires minimum {1,choice,1#1 occurrence|1<{1} occurrences}: {2}", input,
               input.getOccurrences().getMinimum(), size );
    }
}
